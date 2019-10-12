package com.surf.advisor.geolocation.query.service;

import com.surf.advisor.geolocation.api.model.GeoCluster;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Collection;
import java.util.List;

public interface IGeolocationQueryService {

  List<Geolocation> getGeolocations(RectangleGeolocationRequest request);

  Collection<GeoCluster> getGeoClusters(RectangleGeolocationRequest request);

  Geolocation getGeolocation(String objectType, Long objectId);
}
