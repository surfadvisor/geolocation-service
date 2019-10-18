package com.surf.advisor.geolocation.query.clustering;

import static java.lang.Math.abs;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import com.surf.advisor.geolocation.api.model.GeoCluster;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.HashGeolocation;
import com.surf.advisor.geolocation.api.model.LocationId;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ClusteringStrategy<G extends Geolocation> {

  private final RectangleGeolocationRequest request;

  ClusteringStrategy(RectangleGeolocationRequest request) {
    this.request = request;
  }

  abstract Stream<List<G>> clusterStream(@NotNull Collection<HashGeolocation> input);

  public final Collection<GeoCluster> cluster(@NotNull Collection<HashGeolocation> input) {

    log.info("Clustering {} points, queryRecSize: {}, point IDs: {}",
      input.size(), getQueryRecSize(), getIds(input));

    return clusterStream(input)
      .map(this::pointsToCluster)
      .collect(toList());
  }

  double getQueryRecSize() {
    return getAvgRectangleQuerySize(request);
  }

  public static double getAvgRectangleQuerySize(
    @Valid @NotNull RectangleGeolocationRequest request) {

    double latDiff = abs(request.getMinLatitude() - request.getMaxLatitude());
    double lonDiff = abs(request.getMinLongitude() - request.getMaxLongitude());

    return (latDiff + lonDiff) / 2.0;
  }

  Set<String> getIds(@NotNull Collection<? extends Geolocation> input) {
    return input.stream().map(Geolocation::getObjectId).collect(toSet());
  }

  private GeoCluster pointsToCluster(@NotNull List<G> points) {

    double avgLat = 0.0;
    double avgLon = 0.0;

    Set<LocationId> ids = new HashSet<>();

    for (Geolocation point : points) {
      avgLat += point.getLatitude();
      avgLon += point.getLongitude();

      ids.add(LocationId.of(point.getObjectType(), point.getObjectId()));
    }

    avgLat /= points.size();
    avgLon /= points.size();

    return new GeoCluster(ids, avgLat, avgLon);
  }

}
