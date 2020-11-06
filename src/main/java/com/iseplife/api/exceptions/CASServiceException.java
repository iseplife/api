package com.iseplife.api.exceptions;

public class CASServiceException extends RuntimeException {
    public CASServiceException(String message) {
      super(message);
    }

    public CASServiceException(String message, Throwable cause) {
      super(message, cause);
    }
}
