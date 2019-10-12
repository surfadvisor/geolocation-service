package com.surf.advisor.geolocation.query.impl;

import static com.google.common.base.Optional.absent;
import static com.surf.advisor.geolocation.query.clustering.ClusteringStrategy.getAvgRectangleQuerySize;
import static java.lang.Thread.currentThread;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import com.amazonaws.geo.Geo;
import com.amazonaws.geo.GeoConfig;
import com.amazonaws.geo.s2.internal.GeoQueryClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.surf.advisor.geolocation.api.exception.GeoQueryTechnicalException;
import com.surf.advisor.geolocation.api.model.GeoCluster;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.HashGeolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import com.surf.advisor.geolocation.query.clustering.ClusteringStrategy;
import com.surf.advisor.geolocation.query.clustering.DBSCANClusteringStrategy;
import com.surf.advisor.geolocation.query.clustering.GeohashClusteringStrategy;
import com.surf.advisor.geolocation.query.clustering.KMeansClusteringStrategy;
import com.surf.advisor.geolocation.query.service.IGeolocationQueryService;
import com.surf.advisor.geolocation.query.util.GeolocationMappingUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class GeolocationQueryService implements IGeolocationQueryService {

  private static final double SWITCH_CLUSTERING_QUERY_SIZE_THRESHOLD = 5.0;
  private static final int SWITCH_CLUSTERING_POINTS_COUNT_THRESHOLD = 10;

  private final AmazonDynamoDB dbClient;
  private final GeoQueryClient geoQueryClient;
  private final GeoConfig geoConfig;

  private final String tableName;

  @Override
  public Geolocation getGeolocation(String objectType, Long objectId) {

    var request = new QueryRequest()
      .withTableName(tableName)
      .withKeyConditionExpression("#objectType = :type and #objectId = :id")
      .withExpressionAttributeNames(Map.of("#objectType", "objectType", "#objectId", "objectId"))
      .withExpressionAttributeValues(Map.of(
        ":type", new AttributeValue(objectType),
        ":id", new AttributeValue().withN(objectId.toString()))
      );

    return of(request).map(dbClient::query).map(QueryResult::getItems)
      .map(items -> !items.isEmpty() ? items.get(0) : null)
      .map(GeolocationMappingUtils::geolocationOf)
      .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "no such geolocation"));
  }

  @Override
  public List<Geolocation> getGeolocations(RectangleGeolocationRequest request) {
    return performRectangleQuery(request).stream()
      .map(GeolocationMappingUtils::geolocationOf).collect(toList());
  }

  @Override
  public Collection<GeoCluster> getGeoClusters(RectangleGeolocationRequest request) {

    var points = performRectangleQuery(request).stream()
      .map(GeolocationMappingUtils::hashGeolocationOf).collect(toList());

    var strategy = resolveClusteringStrategy(request, points);

    return strategy.cluster(points);
  }

  private ClusteringStrategy<? extends HashGeolocation> resolveClusteringStrategy(
    RectangleGeolocationRequest request, List<HashGeolocation> points) {

    double avgQuerySize = getAvgRectangleQuerySize(request);

    if (avgQuerySize > SWITCH_CLUSTERING_QUERY_SIZE_THRESHOLD) {
      return new GeohashClusteringStrategy(request);
    } else {
      if (points.size() > SWITCH_CLUSTERING_POINTS_COUNT_THRESHOLD) {
        return new KMeansClusteringStrategy(request);
      } else {
        return new DBSCANClusteringStrategy(request);
      }
    }
  }

  private List<Map<String, AttributeValue>> performRectangleQuery(
    RectangleGeolocationRequest request) {
    var geoQueryRequest = new Geo().rectangleQuery(
      new QueryRequest(tableName),
      request.getMinLatitude(),
      request.getMinLongitude(),
      request.getMaxLatitude(),
      request.getMaxLongitude(),
      geoConfig,
      absent());

    List<Map<String, AttributeValue>> ddbResult;

    try {
      ddbResult = geoQueryClient.execute(geoQueryRequest);
    } catch (InterruptedException | ExecutionException e) {
      log.error("Exception while rectangle query: {}", e.getMessage());

      currentThread().interrupt();
      throw new GeoQueryTechnicalException();
    }

    return ofNullable(ddbResult).orElse(List.of());
  }
}
