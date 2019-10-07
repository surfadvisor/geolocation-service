package com.surf.advisor.geolocation.query.strategy;

import static java.lang.Math.abs;

import com.surf.advisor.geolocation.api.model.GeoCluster;
import com.surf.advisor.geolocation.api.model.HashGeolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Collection;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface ClusteringStrategy {

  Collection<GeoCluster> cluster(@NotNull Collection<HashGeolocation> input);

  default double getAvgRectangleQuerySize(@Valid @NotNull RectangleGeolocationRequest request) {
    double latDiff = abs(request.getMinLatitude() - request.getMaxLatitude());
    double lonDiff = abs(request.getMinLongitude() - request.getMaxLongitude());

    return  (latDiff + lonDiff) / 2.0;
  }

}
