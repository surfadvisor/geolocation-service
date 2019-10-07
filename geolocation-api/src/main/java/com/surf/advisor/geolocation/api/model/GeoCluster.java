package com.surf.advisor.geolocation.api.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoCluster {

  private Set<Long> objectIds;
  private Double latitude;
  private Double longitude;

}
