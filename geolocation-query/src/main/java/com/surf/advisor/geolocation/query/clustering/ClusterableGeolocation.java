package com.surf.advisor.geolocation.query.clustering;

import static org.apache.commons.beanutils.BeanUtils.copyProperties;

import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.HashGeolocation;
import java.lang.reflect.InvocationTargetException;
import javax.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@NoArgsConstructor
public class ClusterableGeolocation extends HashGeolocation implements Clusterable {

  @Override
  public double[] getPoint() {
    return new double[]{getLatitude(), getLongitude()};
  }

  static ClusterableGeolocation of(@NotNull Geolocation source) {
    var result = new ClusterableGeolocation();
    try {
      copyProperties(result, source);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.error("Exception while creating clusterable geolocation object: {}", e.getMessage());
    }
    return result;
  }
}
