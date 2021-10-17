package com.iseplife.api.exceptions;

import com.iseplife.api.exceptions.http.HttpInternalServerErrorException;

import java.io.IOException;

public class FileException extends HttpInternalServerErrorException {
  public FileException(String message) {
    super(message);
  }

  public FileException(String s, IOException e) {
    super(s, e);
  }
}
