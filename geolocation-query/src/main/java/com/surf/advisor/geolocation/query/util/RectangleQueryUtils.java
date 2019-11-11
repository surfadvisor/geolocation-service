package com.surf.advisor.geolocation.query.util;

import static com.surf.advisor.geolocation.query.util.RectangleQueryUtils.Side.MAX;
import static com.surf.advisor.geolocation.query.util.RectangleQueryUtils.Side.MIN;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.surf.advisor.geolocation.api.model.RectangleGeolocationRequest;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class RectangleQueryUtils {

  private static final Map<Side, DimensionOperators> LATITUDE_OPERATORS = Map.of(
    MIN, DimensionOperators.of(
      RectangleGeolocationRequest::getMinLatitude,
      RectangleGeolocationRequest::setMinLatitude
    ),
    MAX, DimensionOperators.of(
      RectangleGeolocationRequest::getMaxLatitude,
      RectangleGeolocationRequest::setMaxLatitude
    )
  );

  private static final Map<Side, DimensionOperators> LONGITUDE_OPERATORS = Map.of(
    MIN, DimensionOperators.of(
      RectangleGeolocationRequest::getMinLongitude,
      RectangleGeolocationRequest::setMinLongitude
    ),
    MAX, DimensionOperators.of(
      RectangleGeolocationRequest::getMaxLongitude,
      RectangleGeolocationRequest::setMaxLongitude
    )
  );

  public boolean emptyRectangleQuery(RectangleGeolocationRequest rec) {
    return Stream.of(LATITUDE_OPERATORS, LONGITUDE_OPERATORS)
      .map(op -> op.get(MAX).get(rec) - op.get(MIN).get(rec))
      .anyMatch(diff -> diff == 0);
  }

  public static void adjustRectangleQuery(RectangleGeolocationRequest rec) {

    var avgSize = Stream.of(LATITUDE_OPERATORS, LONGITUDE_OPERATORS)
      .map(op -> op.get(MAX).get(rec) - op.get(MIN).get(rec))
      .map(Math::abs)
      .filter(diff -> diff != 0.0)
      .mapToDouble(d -> d)
      .average();

    double minDimensionDiff = avgSize.orElse(0.01) * 0.001;

    log.debug("Query dimensions average size: {}, min diff: {}", avgSize, minDimensionDiff);

    Stream.of(LATITUDE_OPERATORS, LONGITUDE_OPERATORS).forEach(op -> {
      var a = op.get(MAX).get(rec);
      var b = op.get(MIN).get(rec);

      var min = min(a, b);
      var max = max(a, b);

      max = abs(max - min) > minDimensionDiff ? max : min + minDimensionDiff;

      op.get(MIN).set(rec, min);
      op.get(MAX).set(rec, max);
    });

    log.debug("Rectangle adjustment result: {}", rec);
  }

  enum Side {
    MIN, MAX
  }

  @RequiredArgsConstructor(staticName = "of")
  private static class DimensionOperators {

    private final RectangleGetter getter;
    private final RectangleSetter setter;

    Double get(RectangleGeolocationRequest rec) {
      return getter.apply(rec);
    }

    void set(RectangleGeolocationRequest rec, Double value) {
      setter.accept(rec, value);
    }
  }

  private interface RectangleGetter extends Function<RectangleGeolocationRequest, Double> {

  }

  private interface RectangleSetter extends BiConsumer<RectangleGeolocationRequest, Double> {

  }
}
