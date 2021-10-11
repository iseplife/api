package com.iseplife.api.exceptions;

public class CommentMaxDepthException extends HttpInternalServerErrorException {

  public CommentMaxDepthException(String message) {
    super(message);
  }

  public CommentMaxDepthException(String s, Throwable e) {
    super(s, e);
  }
}
