package com.surf.advisor.geolocation.api.model;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RectangleGeolocationRequest {

  @NotNull
  private Double minLatitude;

  @NotNull
  private Double minLongitude;

  @NotNull
  private Double maxLatitude;

  @NotNull
  private Double maxLongitude;

}
