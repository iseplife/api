package com.iseplife.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Guillaume on 24/10/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class AuthException extends RuntimeException {
  public AuthException(String message) {
    super(message);
  }

  public AuthException(String message, Throwable cause) {
    super(message, cause);
  }
}
