package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.output.ApiError;
import com.saasquatch.sdk.util.SaaSquatchHttpResponse;
import javax.annotation.Nonnull;

/**
 * {@link Exception} type representing a "handled" API error, i.e. the HTTP request is successful,
 * and we are able to receive a good {@link ApiError}.
 *
 * @author sli
 */
public final class SaaSquatchApiException extends SaaSquatchHttpResponseEnclosedException {

  @Nonnull
  private final ApiError apiError;

  @Internal
  public SaaSquatchApiException(@Nonnull ApiError apiError, @Nonnull SaaSquatchHttpResponse httpResponse) {
    super(apiError.getMessage(), httpResponse);
    this.apiError = apiError;
  }

  @Nonnull
  public ApiError getApiError() {
    return apiError;
  }

}
