package com.amazonaws.geo;

import com.dashlabs.dash.geo.AbstractGeoQueryHelper;
import com.dashlabs.dash.geo.model.GeohashRange;
import com.dashlabs.dash.geo.s2.internal.S2Manager;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.geometry.S2LatLngRect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mpuri on 3/25/14
 * 
 * Corkhounds.com altered this class to add copy over the FilterExpression and 
 * Expression Attributes as necessary when building a QueryRequest. 
 */
public class GeoQueryHelper extends AbstractGeoQueryHelper {

    public GeoQueryHelper(S2Manager s2Manager) {
        super(s2Manager);
    }

    /**
     * For the given <code>QueryRequest</code> query and the boundingBox, this method creates a collection of queries
     * that are decorated with geo attributes to enable geo-spatial querying.
     *
     * @param query       the original query request
     * @param boundingBox the bounding lat long rectangle of the geo query
     * @param config      the config containing caller's geo config, example index name, etc.
     * @param compositeKeyValue the value of the column that is used in the construction of the composite hash key(geoHashKey + someOtherColumnValue).
     *                          This is needed when constructing queries that need a composite hash key.
     *                          For eg. Fetch an item where lat/long is 23.78787, -70.6767 AND category = 'restaurants'
     * @return queryRequests an immutable collection of <code>QueryRequest</code> that are now "geo enabled"
     */
    public List<QueryRequest> generateGeoQueries(QueryRequest query, S2LatLngRect boundingBox, GeoConfig config, Optional<String> compositeKeyValue) {
        List<GeohashRange> outerRanges = getGeoHashRanges(boundingBox);
        List<QueryRequest> queryRequests = new ArrayList<QueryRequest>(outerRanges.size());
        //Create multiple queries based on the geo ranges derived from the bounding box
        for (GeohashRange outerRange : outerRanges) {
            List<GeohashRange> geohashRanges = outerRange.trySplit(config.getGeoHashKeyLength(), s2Manager);
            for (GeohashRange range : geohashRanges) {
                //Make a copy of the query request to retain original query attributes like table name, etc.
                QueryRequest queryRequest = copyQueryRequest(query);

                //generate the hash key for the global secondary index
                long geohashKey = s2Manager.generateHashKey(range.getRangeMin(), config.getGeoHashKeyLength());
                Map<String, Condition> keyConditions = new HashMap<String, Condition>(2, 1.0f);

                //Construct the hashKey condition
                Condition geoHashKeyCondition;
                if (config.getHashKeyDecorator().isPresent() && compositeKeyValue.isPresent()) {
                    String compositeHashKey = config.getHashKeyDecorator().get().decorate(compositeKeyValue.get(), geohashKey);
                    geoHashKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
                            .withAttributeValueList(new AttributeValue().withS(compositeHashKey));
                } else {
                    geoHashKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.EQ)
                            .withAttributeValueList(new AttributeValue().withN(String.valueOf(geohashKey)));
                }
                keyConditions.put(config.getGeoHashKeyColumn(), geoHashKeyCondition);

                //generate the geo hash range
                AttributeValue minRange = new AttributeValue().withN(Long.toString(range.getRangeMin()));
                AttributeValue maxRange = new AttributeValue().withN(Long.toString(range.getRangeMax()));

                Condition geoHashCondition = new Condition().withComparisonOperator(ComparisonOperator.BETWEEN)
                        .withAttributeValueList(minRange, maxRange);
                keyConditions.put(config.getGeoHashColumn(), geoHashCondition);

                queryRequest.withKeyConditions(keyConditions)
                        .withIndexName(config.getGeoIndexName());
                queryRequests.add(queryRequest);
            }
        }
        return ImmutableList.copyOf(queryRequests);
    }

    /**
     * Creates a copy of the provided <code>QueryRequest</code> queryRequest
     *
     * @param queryRequest
     * @return a new
     */
    private QueryRequest copyQueryRequest(QueryRequest queryRequest) {
        QueryRequest copiedQueryRequest = new QueryRequest().withAttributesToGet(queryRequest.getAttributesToGet())
                .withConsistentRead(queryRequest.getConsistentRead())
                .withExclusiveStartKey(queryRequest.getExclusiveStartKey())
                .withIndexName(queryRequest.getIndexName())
                .withKeyConditions(queryRequest.getKeyConditions())
                .withLimit(queryRequest.getLimit())
                .withReturnConsumedCapacity(queryRequest.getReturnConsumedCapacity())
                .withScanIndexForward(queryRequest.getScanIndexForward())
                .withSelect(queryRequest.getSelect())
                .withAttributesToGet(queryRequest.getAttributesToGet())
                .withTableName(queryRequest.getTableName())
                .withFilterExpression(queryRequest.getFilterExpression())
                .withExpressionAttributeNames(queryRequest.getExpressionAttributeNames())
                .withExpressionAttributeValues(queryRequest.getExpressionAttributeValues());

        return copiedQueryRequest;
    }
}
