package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.NoExternalImpl;

/**
 * Base {@link Exception} type for SaaSquatch Java SDK
 *
 * @author sli
 */
@NoExternalImpl
public abstract class SaaSquatchException extends RuntimeException {

  SaaSquatchException() {}

  SaaSquatchException(String message) {
    super(message);
  }

  SaaSquatchException(String message, Throwable cause) {
    super(message, cause);
  }

  SaaSquatchException(Throwable cause) {
    super(cause);
  }

}
