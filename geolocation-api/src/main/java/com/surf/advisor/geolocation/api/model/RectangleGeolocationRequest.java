package com.surf.advisor.geolocation.api.model;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
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
