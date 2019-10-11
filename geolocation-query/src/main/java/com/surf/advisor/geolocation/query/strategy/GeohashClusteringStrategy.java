package com.surf.advisor.geolocation.query.strategy;

import static java.lang.Math.round;
import static java.util.stream.Collectors.groupingBy;

import com.surf.advisor.geolocation.api.model.HashGeolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeohashClusteringStrategy extends ClusteringStrategy<HashGeolocation> {

  public GeohashClusteringStrategy(RectangleGeolocationRequest request) {
    super(request);
  }

  @Override
  Stream<List<HashGeolocation>> clusterStream(@NotNull Collection<HashGeolocation> input) {

    int geoLength = (int) round(5.0 - 0.0333 * getQueryRecSize());

    log.info("Applying geohash clustering with grouping length: {}", geoLength);

    return input.stream()
      .collect(groupingBy(point -> point.getGeoHash().toString().substring(0, geoLength)))
      .values().stream();
  }

}
