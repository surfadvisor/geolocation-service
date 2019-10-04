package com.surf.advisor.spot.command.impl;

import com.amazonaws.geo.Geo;
import com.amazonaws.geo.GeoConfig;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.surf.advisor.spot.api.PutSpotRequest;
import com.surf.advisor.spot.command.service.ISpotCommandService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class SpotCommandService implements ISpotCommandService {

  private final AmazonDynamoDB ddb;
  private final GeoConfig geoConfig;

  @Override
  public void putSpot(PutSpotRequest request) {

    var item = Map.of(
      "spotId", new AttributeValue(String.valueOf(request.getSpotId())),
      "name", new AttributeValue(String.valueOf(request.getSpotName()))
    );

    var ddbRequest = new PutItemRequest("SPOT", new HashMap<>(item));

    ddbRequest = new Geo().putItemRequest(ddbRequest,
      request.getLatitude(), request.getLongitude(), List.of(geoConfig));

    ddb.putItem(ddbRequest);
  }
}
