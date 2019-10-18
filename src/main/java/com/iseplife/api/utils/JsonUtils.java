package com.iseplife.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iseplife.api.exceptions.IllegalArgumentException;
import com.iseplife.api.exceptions.IllegalArgumentException;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by Guillaume on 25/10/2017.
 * back
 */
@Component
public class JsonUtils {
  public <T> T deserialize(String raw, Class<T> type) {
    try {
      return new ObjectMapper().readValue(raw, type);
    } catch (IOException e) {
      throw new IllegalArgumentException("could not deserialize data", e);
    }
  }

  public String serialize(Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (IOException e) {
      throw new IllegalArgumentException("could not serialize data", e);
    }
  }
}
