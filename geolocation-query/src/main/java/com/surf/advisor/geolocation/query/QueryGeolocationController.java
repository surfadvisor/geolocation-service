package com.surf.advisor.geolocation.query;

import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import com.surf.advisor.geolocation.query.service.IGeolocationQueryService;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class QueryGeolocationController {

  private final IGeolocationQueryService queryService;

  @GetMapping("/geolocations")
  public List<Geolocation> getGeolocations(
    @Valid @NotNull @RequestBody RectangleGeolocationRequest request) {

    return queryService.getGeolocations(request);
  }

}
