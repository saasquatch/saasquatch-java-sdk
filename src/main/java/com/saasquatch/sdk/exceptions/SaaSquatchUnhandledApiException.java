package com.saasquatch.sdk.exceptions;

/**
 * {@link Exception} type representing an unhandled API error, i.e. the HTTP request is successful,
 * but we are unable to extract a good {@link com.saasquatch.sdk.output.ApiError}.
 *
 * @author sli
 */
public class SaaSquatchUnhandledApiException extends SaaSquatchException {

  private final String bodyText;

  public SaaSquatchUnhandledApiException(String bodyText) {
    super();
    this.bodyText = bodyText;
  }

  public String getBodyText() {
    return bodyText;
  }
}
