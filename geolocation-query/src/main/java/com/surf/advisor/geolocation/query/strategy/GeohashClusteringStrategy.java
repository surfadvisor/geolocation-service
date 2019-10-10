package com.surf.advisor.geolocation.query.strategy;

import static java.lang.Math.round;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import com.surf.advisor.geolocation.api.model.GeoCluster;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.HashGeolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class GeohashClusteringStrategy implements ClusteringStrategy {

  private final RectangleGeolocationRequest request;

  @Override
  public Collection<GeoCluster> cluster(@NotNull Collection<HashGeolocation> input) {

    double queryRecSize = getAvgRectangleQuerySize(request);

    int geoLength = (int) round(5.0 - 0.0333 * queryRecSize);

    log.info("Applying geohash clustering to {} points with grouping length: {} (queryRecSize: {})",
      input.size(), geoLength, queryRecSize);

    return input.stream()
      .collect(groupingBy(point -> point.getGeoHash().toString().substring(0, geoLength)))
      .values().stream()
      .map(this::pointsToCluster)
      .collect(toList());
  }

  private GeoCluster pointsToCluster(@NotNull List<HashGeolocation> points) {

    double avgLat = 0.0;
    double avgLon = 0.0;

    Set<Long> ids = new HashSet<>();

    for (Geolocation point : points) {
      avgLat += point.getLatitude();
      avgLon += point.getLongitude();

      ids.add(point.getObjectId());
    }

    avgLat /= points.size();
    avgLon /= points.size();

    return new GeoCluster(ids, avgLat, avgLon);
  }
}
