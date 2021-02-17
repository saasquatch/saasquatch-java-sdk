package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.annotations.NoExternalImpl;

/**
 * Base {@link Exception} type for SaaSquatch Java SDK
 *
 * @author sli
 */
@NoExternalImpl
public abstract class SaaSquatchException extends RuntimeException {

  @Internal
  SaaSquatchException() {}

  @Internal
  SaaSquatchException(String message) {
    super(message);
  }

  @Internal
  SaaSquatchException(String message, Throwable cause) {
    super(message, cause);
  }

  @Internal
  SaaSquatchException(Throwable cause) {
    super(cause);
  }

}
