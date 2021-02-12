package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.annotations.NoExternalImpl;
import com.saasquatch.sdk.http.SaaSquatchHttpResponse;
import javax.annotation.Nonnull;

/**
 * Exception type with an enclosed {@link SaaSquatchHttpResponse}.
 *
 * @author sli
 */
@NoExternalImpl
public abstract class SaaSquatchHttpResponseEnclosedException extends SaaSquatchException {

  private final SaaSquatchHttpResponse httpResponse;

  @Internal
  public SaaSquatchHttpResponseEnclosedException(@Nonnull SaaSquatchHttpResponse httpResponse) {
    this.httpResponse = httpResponse;
  }

  @Internal
  public SaaSquatchHttpResponseEnclosedException(String message,
      @Nonnull SaaSquatchHttpResponse httpResponse) {
    super(message);
    this.httpResponse = httpResponse;
  }

  @Internal
  public SaaSquatchHttpResponseEnclosedException(String message, Throwable cause,
      @Nonnull SaaSquatchHttpResponse httpResponse) {
    super(message, cause);
    this.httpResponse = httpResponse;
  }

  @Internal
  public SaaSquatchHttpResponseEnclosedException(Throwable cause,
      @Nonnull SaaSquatchHttpResponse httpResponse) {
    super(cause);
    this.httpResponse = httpResponse;
  }

  @Nonnull
  public SaaSquatchHttpResponse getHttpResponse() {
    return httpResponse;
  }

}
