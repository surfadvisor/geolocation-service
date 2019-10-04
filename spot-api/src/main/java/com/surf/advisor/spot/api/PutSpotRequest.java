package com.surf.advisor.spot.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PutSpotRequest {

  private Long spotId;
  private Double latitude;
  private Double longitude;

  private String spotName;

}
