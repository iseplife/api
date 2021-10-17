package com.iseplife.api.exceptions.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Guillaume on 24/10/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class HttpUnauthorizedException extends RuntimeException {
  public HttpUnauthorizedException(String message) {
    super(message);
  }

  public HttpUnauthorizedException(String message, Throwable cause) {
    super(message, cause);
  }
}
