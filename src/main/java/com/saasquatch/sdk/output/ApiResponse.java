package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.annotations.NoExternalImpl;
import com.saasquatch.sdk.util.SaaSquatchHttpResponse;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an API response from SaaSquatch.
 *
 * @author sli
 */
@NoExternalImpl
public abstract class ApiResponse<T> {

  // Lazy init. Not part of the lazy init of data and error, since those depend on bodyText.
  private String bodyText;
  private final SaaSquatchHttpResponse response;
  private final boolean hasDataOverride;
  private final T dataOverride;

  @Internal
  ApiResponse(@Nonnull SaaSquatchHttpResponse response) {
    this.response = response;
    this.hasDataOverride = false;
    this.dataOverride = null;
  }

  @Internal
  ApiResponse(@Nonnull SaaSquatchHttpResponse response, @Nullable T dataOverride) {
    this.response = response;
    this.hasDataOverride = true;
    this.dataOverride = dataOverride;
  }

  @Nonnull
  public SaaSquatchHttpResponse getHttpResponse() {
    return response;
  }

  @Nullable
  public final T getData() {
    if (hasDataOverride) {
      return dataOverride;
    }
    return buildData();
  }

  protected abstract T buildData();

}
