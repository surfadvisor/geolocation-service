package com.surf.advisor.geolocation.query.impl;

import static com.google.common.base.Optional.absent;
import static java.lang.Thread.currentThread;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import com.amazonaws.geo.Geo;
import com.amazonaws.geo.GeoConfig;
import com.amazonaws.geo.s2.internal.GeoQueryClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.surf.advisor.geolocation.api.exception.GeoQueryTechnicalException;
import com.surf.advisor.geolocation.api.model.GeoCluster;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import com.surf.advisor.geolocation.query.service.IGeolocationQueryService;
import com.surf.advisor.geolocation.query.strategy.GeohashClusteringStrategy;
import com.surf.advisor.geolocation.query.util.GeolocationMappingUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class GeolocationQueryService implements IGeolocationQueryService {

  private final GeoQueryClient geoQueryClient;
  private final GeoConfig geoConfig;

  @Override
  public List<Geolocation> getGeolocations(RectangleGeolocationRequest request) {
    return performRectangleQuery(request).stream()
      .map(GeolocationMappingUtils::geolocationOf).collect(toList());
  }

  @Override
  public Collection<GeoCluster> getGeoClusters(RectangleGeolocationRequest request) {
    var points = performRectangleQuery(request).stream()
      .map(GeolocationMappingUtils::hashGeolocationOf).collect(toList());

    return new GeohashClusteringStrategy(request).cluster(points);
  }

  private List<Map<String, AttributeValue>> performRectangleQuery(
    RectangleGeolocationRequest request) {
    var geoQueryRequest = new Geo().rectangleQuery(
      new QueryRequest("GEOLOCATION"),
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
