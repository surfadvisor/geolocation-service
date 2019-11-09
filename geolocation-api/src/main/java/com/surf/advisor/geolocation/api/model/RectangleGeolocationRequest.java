package com.surf.advisor.geolocation.api.model;

import javax.validation.constraints.NotNull;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RectangleGeolocationRequest implements Serializable {

  @NotNull
  private Double minLatitude;

  @NotNull
  private Double minLongitude;

  @NotNull
  private Double maxLatitude;

  @NotNull
  private Double maxLongitude;

}
