package com.iseplife.api.utils;

import java.util.HashMap;
import java.util.Map;

public class ObjectUtils {

  public static Map asMap(Object... values) {
    if (values.length % 2 != 0) {
      throw new RuntimeException("Usage - (key, value, key, value, ...)");
    } else {
      Map result = new HashMap<>(values.length / 2);

      for(int i = 0; i < values.length; i += 2) {
        result.put(values[i], values[i + 1]);
      }

      return result;
    }
  }
}
