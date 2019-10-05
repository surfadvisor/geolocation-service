package com.surf.advisor.geolocation.command.impl;

import com.amazonaws.geo.Geo;
import com.amazonaws.geo.GeoConfig;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.command.service.IGeolocationCommandService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class GeolocationCommandService implements IGeolocationCommandService {

  private final AmazonDynamoDB ddb;
  private final GeoConfig geoConfig;

  @Override
  public void putGeolocation(Geolocation request) {

    var item = Map.of(
      "objectId", new AttributeValue().withN(request.getObjectId().toString()),
      "objectType", new AttributeValue(request.getObjectType()),
      "latitude", new AttributeValue().withN(request.getLatitude().toString()),
      "longitude", new AttributeValue().withN(request.getLongitude().toString())
    );

    var ddbRequest = new PutItemRequest("GEOLOCATION", new HashMap<>(item));

    ddbRequest = new Geo().putItemRequest(ddbRequest,
      request.getLatitude(), request.getLongitude(), List.of(geoConfig));

    ddb.putItem(ddbRequest);
  }
}
