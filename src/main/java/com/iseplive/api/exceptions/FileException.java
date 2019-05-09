package com.iseplive.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * Created by Guillaume on 17/08/2017.
 * back
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileException extends RuntimeException {
  public FileException(String message) {
    super(message);
  }

  public FileException(String s, IOException e) {
    super(s, e);
  }
}
