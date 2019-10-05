package com.surf.advisor.geolocation.command;

import com.surf.advisor.geolocation.api.Geolocation;
import com.surf.advisor.geolocation.command.service.IGeolocationCommandService;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class CommandGeolocationController {

  private final IGeolocationCommandService commandService;

  @PutMapping("/geolocations")
  public void putGeolocation(@Valid @NotNull @RequestBody Geolocation request) {
    commandService.putGeolocation(request);
  }

}
