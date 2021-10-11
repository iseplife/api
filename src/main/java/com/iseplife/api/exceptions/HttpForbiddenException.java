package com.iseplife.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Guillaume on 24/10/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class HttpForbiddenException extends RuntimeException {
  public HttpForbiddenException(String message) {
    super(message);
  }

  public HttpForbiddenException(String message, Throwable cause) {
    super(message, cause);
  }
}
