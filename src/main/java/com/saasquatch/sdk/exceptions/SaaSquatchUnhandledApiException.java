package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.Internal;

/**
 * {@link Exception} type representing an unhandled API error, i.e. the HTTP request is successful,
 * but we are unable to extract a good {@link com.saasquatch.sdk.output.ApiError}.
 *
 * @author sli
 */
public final class SaaSquatchUnhandledApiException extends SaaSquatchException {

  private final String bodyText;

  @Internal
  public SaaSquatchUnhandledApiException(String bodyText) {
    super();
    this.bodyText = bodyText;
  }

  public String getBodyText() {
    return bodyText;
  }

}
