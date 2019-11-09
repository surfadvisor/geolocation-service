package com.surf.advisor.geolocation.api.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class LocationId implements Serializable {

  private final String objectType;
  private final String objectId;

  @Override
  public String toString() {
    return objectType + "#" + objectId;
  }
}
