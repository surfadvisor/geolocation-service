package com.surf.advisor.geolocation.api.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"objectId", "objectType"})
public class Geolocation implements Serializable {

  @NotNull
  @NotEmpty
  private String objectId;

  @NotNull
  @NotEmpty
  private String objectType;

  @NotNull
  private Double latitude;

  @NotNull
  private Double longitude;

}
