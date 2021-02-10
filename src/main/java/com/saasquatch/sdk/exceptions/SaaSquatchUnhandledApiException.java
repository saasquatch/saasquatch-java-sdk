package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.util.SaaSquatchHttpResponse;
import javax.annotation.Nonnull;

/**
 * {@link Exception} type representing an unhandled API error, i.e. the HTTP request is successful,
 * but we are unable to extract a good {@link com.saasquatch.sdk.output.ApiError}.
 *
 * @author sli
 */
public final class SaaSquatchUnhandledApiException extends SaaSquatchHttpResponseEnclosedException {

  @Internal
  public SaaSquatchUnhandledApiException(@Nonnull SaaSquatchHttpResponse httpResponse) {
    super(httpResponse);
  }

}
