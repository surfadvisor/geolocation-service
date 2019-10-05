package com.surf.advisor.geolocation.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HashGeolocation extends Geolocation {

  private Long geoHashKey;
  private Long geoHash;

}
