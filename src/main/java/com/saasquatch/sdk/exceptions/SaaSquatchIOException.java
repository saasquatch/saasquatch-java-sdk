package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.Internal;

/**
 * {@link Exception} type representing an IO related error like connection timeouts.
 *
 * @author sli
 */
public final class SaaSquatchIOException extends SaaSquatchException {

  @Internal
  public SaaSquatchIOException(String message, Throwable cause) {
    super(message, cause);
  }

}
