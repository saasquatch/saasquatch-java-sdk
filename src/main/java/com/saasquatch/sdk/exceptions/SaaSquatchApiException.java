package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.output.ApiError;
import javax.annotation.Nonnull;

/**
 * {@link Exception} type representing a "handled" API error, i.e. the HTTP request is successful,
 * and we are able to receive a good {@link ApiError}.
 *
 * @author sli
 */
public class SaaSquatchApiException extends SaaSquatchException {

  @Nonnull
  private final ApiError apiError;
  private final String bodyText;

  @Internal
  public SaaSquatchApiException(@Nonnull ApiError apiError, String bodyText) {
    super(apiError.getMessage());
    this.apiError = apiError;
    this.bodyText = bodyText;
  }

  @Nonnull
  public ApiError getApiError() {
    return apiError;
  }

  public String getBodyText() {
    return bodyText;
  }
}
