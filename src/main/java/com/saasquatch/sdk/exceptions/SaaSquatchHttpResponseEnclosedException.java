package com.saasquatch.sdk.exceptions;

import com.saasquatch.sdk.annotations.NoExternalImpl;
import com.saasquatch.sdk.util.SaaSquatchHttpResponse;
import javax.annotation.Nonnull;

/**
 * Exception type with an enclosed {@link SaaSquatchHttpResponse}.
 *
 * @author sli
 */
@NoExternalImpl
public abstract class SaaSquatchHttpResponseEnclosedException extends SaaSquatchException {

  private final SaaSquatchHttpResponse httpResponse;

  public SaaSquatchHttpResponseEnclosedException(@Nonnull SaaSquatchHttpResponse httpResponse) {
    this.httpResponse = httpResponse;
  }

  public SaaSquatchHttpResponseEnclosedException(String message,
      @Nonnull SaaSquatchHttpResponse httpResponse) {
    super(message);
    this.httpResponse = httpResponse;
  }

  public SaaSquatchHttpResponseEnclosedException(String message, Throwable cause,
      @Nonnull SaaSquatchHttpResponse httpResponse) {
    super(message, cause);
    this.httpResponse = httpResponse;
  }

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
