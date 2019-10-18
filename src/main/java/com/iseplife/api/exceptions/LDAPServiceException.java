package com.iseplife.api.exceptions;

public class LDAPServiceException extends RuntimeException {
    public LDAPServiceException(String message) {
      super(message);
    }

    public LDAPServiceException(String message, Throwable cause) {
      super(message, cause);
    }
}
