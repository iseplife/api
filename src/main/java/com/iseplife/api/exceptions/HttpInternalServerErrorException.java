package com.iseplife.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Guillaume on 24/10/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class HttpInternalServerErrorException extends RuntimeException {
  public HttpInternalServerErrorException(String message) {
    super(message);
  }

  public HttpInternalServerErrorException(String message, Throwable cause) {
    super(message, cause);
  }
}
