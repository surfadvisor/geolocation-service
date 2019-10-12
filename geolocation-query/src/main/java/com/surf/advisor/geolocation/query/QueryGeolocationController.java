package com.surf.advisor.geolocation.query;

import com.surf.advisor.geolocation.api.model.GeoCluster;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import com.surf.advisor.geolocation.query.service.IGeolocationQueryService;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/geolocations/{objectType}/{objectId}")
  public Geolocation getGeolocation(@NotEmpty @PathVariable("objectType") String objectType,
                                    @NotNull @Min(0) @PathVariable("objectId") Long objectId) {
    return queryService.getGeolocation(objectType, objectId);
  }

  @GetMapping("/geolocations/clustered")
  public Collection<GeoCluster> getGeoClusters(
    @Valid @NotNull @RequestBody RectangleGeolocationRequest request) {

    return queryService.getGeoClusters(request);
  }

}
