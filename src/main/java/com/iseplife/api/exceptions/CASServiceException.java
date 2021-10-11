package com.iseplife.api.exceptions;

import com.iseplife.api.exceptions.http.HttpInternalServerErrorException;

public class CASServiceException extends HttpInternalServerErrorException {
    public CASServiceException(String message) {
      super(message);
    }

    public CASServiceException(String message, Throwable cause) {
      super(message, cause);
    }
}
