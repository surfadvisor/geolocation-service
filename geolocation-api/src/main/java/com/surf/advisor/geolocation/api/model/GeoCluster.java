package com.surf.advisor.geolocation.api.model;

import java.io.Serializable;
import java.util.Set;

import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(of = "objectIds")
public class GeoCluster implements Serializable {

  private Set<LocationId> objectIds;
  private Double latitude;
  private Double longitude;

}
