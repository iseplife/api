package com.iseplife.api.exceptions.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class HttpNotFoundException extends RuntimeException {
  public HttpNotFoundException(String message) {
    super(message);
  }
}
