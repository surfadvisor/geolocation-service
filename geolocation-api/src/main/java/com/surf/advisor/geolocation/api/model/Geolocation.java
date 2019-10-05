package com.surf.advisor.geolocation.api.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"objectId", "objectType"})
public class Geolocation {

  @NotNull
  @Min(0)
  private Long objectId;

  @NotNull
  @NotEmpty
  private String objectType;

  @NotNull
  private Double latitude;

  @NotNull
  private Double longitude;

}
