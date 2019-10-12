package com.surf.advisor.geolocation.api.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeoCluster {

  private Set<LocationId> objectIds;
  private Double latitude;
  private Double longitude;

}
