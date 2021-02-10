package com.saasquatch.sdk.exceptions;

/**
 * Base {@link Exception} type for SaaSquatch Java SDK
 *
 * @author sli
 */
public class SaaSquatchException extends RuntimeException {

  public SaaSquatchException() {
  }

  public SaaSquatchException(String message) {
    super(message);
  }

  public SaaSquatchException(String message, Throwable cause) {
    super(message, cause);
  }

  public SaaSquatchException(Throwable cause) {
    super(cause);
  }

}
