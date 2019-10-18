package com.surf.advisor.geolocation.query.util;

import static java.util.Optional.of;
import static java.util.function.Function.identity;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.surf.advisor.geolocation.api.model.Geolocation;
import com.surf.advisor.geolocation.api.model.HashGeolocation;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GeolocationMappingUtils {

  public static Geolocation geolocationOf(Map<String, AttributeValue> item) {
    if (item == null) {
      return null;
    }

    var result = new Geolocation();

    setGeolocationValues(item, result);

    return result;
  }

  public static HashGeolocation hashGeolocationOf(Map<String, AttributeValue> item) {
    if (item == null) {
      return null;
    }

    var result = new HashGeolocation();

    setGeolocationValues(item, result);
    map("geoHashKey", item, AttributeValue::getN, Long::valueOf, result::setGeoHashKey);
    map("geoHash", item, AttributeValue::getN, Long::valueOf, result::setGeoHash);

    return result;
  }

  private static void setGeolocationValues(Map<String, AttributeValue> item, Geolocation result) {
    map("objectId", item, AttributeValue::getS, identity(), result::setObjectId);
    map("objectType", item, AttributeValue::getS, identity(), result::setObjectType);
    map("latitude", item, AttributeValue::getN, Double::valueOf, result::setLatitude);
    map("longitude", item, AttributeValue::getN, Double::valueOf, result::setLongitude);
  }

  private static <T> void map(@NotNull String key,
                              @NotNull Map<String, AttributeValue> item,
                              @NotNull Function<AttributeValue, String> getStrVal,
                              @NotNull Function<String, T> toTargetType,
                              @NotNull Consumer<T> setter) {

    of(key).map(item::get).map(getStrVal).map(toTargetType).ifPresent(setter);
  }

}
