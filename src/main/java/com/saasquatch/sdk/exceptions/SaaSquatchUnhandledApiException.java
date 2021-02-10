package com.saasquatch.sdk.exceptions;

/**
 * {@link Exception} type representing an unhandled API error, i.e. the HTTP request is successful,
 * but we are unable to extract a good {@link com.saasquatch.sdk.output.ApiError}.
 *
 * @author sli
 */
public class SaaSquatchUnhandledApiException extends SaaSquatchException {

  public SaaSquatchUnhandledApiException(String message) {
    super(message);
  }

  public SaaSquatchUnhandledApiException(String message, Throwable cause) {
    super(message, cause);
  }

  public SaaSquatchUnhandledApiException(Throwable cause) {
    super(cause);
  }

}
