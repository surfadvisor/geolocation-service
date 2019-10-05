package com.surf.advisor.geolocation.query.service;

import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.List;

public interface IGeolocationQueryService {

  List<Geolocation> getGeolocations(RectangleGeolocationRequest request);

}
