package com.iseplive.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Guillaume on 31/07/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalArgumentException extends RuntimeException {
  public IllegalArgumentException(String message) {
    super(message);
  }

  public IllegalArgumentException(String message, Throwable cause) {
    super(message, cause);
  }

}
