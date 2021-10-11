package com.iseplife.api.exceptions.http;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class HttpBadRequestException extends RuntimeException {
  public HttpBadRequestException(String message) {
    super(message);
  }

  public HttpBadRequestException(String message, Throwable cause) {
    super(message, cause);
  }

}
