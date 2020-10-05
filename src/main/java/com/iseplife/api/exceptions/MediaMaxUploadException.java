package com.iseplife.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class MediaMaxUploadException extends RuntimeException {
  public MediaMaxUploadException(String message) {
    super(message);
  }

  public MediaMaxUploadException(String message, Throwable cause) {
    super(message, cause);
  }
}
