package com.iseplife.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CommentMaxDepthException extends RuntimeException {

  public CommentMaxDepthException(String message) {
    super(message);
  }

  public CommentMaxDepthException(String s, Throwable e) {
    super(s, e);
  }
}
