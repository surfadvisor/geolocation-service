package com.surf.advisor.geolocation.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(staticName = "of")
public class LocationId {

  private final String objectType;
  private final String objectId;

  @Override
  public String toString() {
    return objectType + "#" + objectId;
  }
}
