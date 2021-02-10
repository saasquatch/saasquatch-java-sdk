package com.saasquatch.sdk.exceptions;

/**
 * {@link Exception} type representing an IO related error like connection timeouts.
 *
 * @author sli
 */
public class SaaSquatchIOException extends SaaSquatchException {

  public SaaSquatchIOException(String message, Throwable cause) {
    super(message, cause);
  }

}
