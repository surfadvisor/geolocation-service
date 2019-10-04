package com.surf.advisor.spot.command;

import com.surf.advisor.spot.api.PutSpotRequest;
import com.surf.advisor.spot.command.service.ISpotCommandService;
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
public class CommandSpotController {

  private final ISpotCommandService commandService;

  @PutMapping("/spots")
  public void putSpot(@Valid @NotNull @RequestBody PutSpotRequest request) {
    commandService.putSpot(request);
  }

}
