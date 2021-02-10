package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.annotations.NoExternalImpl;
import com.saasquatch.sdk.internal.InternalUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.Header;

/**
 * Represents an API response from SaaSquatch.
 *
 * @author sli
 */
@NoExternalImpl
public abstract class ApiResponse<T> {

  // Lazy init. Not part of the lazy init of data and error, since those depend on bodyText.
  private String bodyText;
  private final SimpleHttpResponse response;
  private final boolean hasDataOverride;
  private final T dataOverride;

  @Internal
  ApiResponse(@Nonnull SimpleHttpResponse response) {
    this.response = response;
    this.hasDataOverride = false;
    this.dataOverride = null;
  }

  @Internal
  ApiResponse(@Nonnull SimpleHttpResponse response, @Nullable T dataOverride) {
    this.response = response;
    this.hasDataOverride = true;
    this.dataOverride = dataOverride;
  }

  public final int getStatusCode() {
    return response.getCode();
  }

  @Internal
  public SimpleHttpResponse getResponse() {
    return response;
  }

  @Nullable
  public final String getFirstHeader(String headerName) {
    final Header header = response.getFirstHeader(headerName);
    return header == null ? null : header.getValue();
  }

  @Nullable
  public final T getData() {
    if (hasDataOverride) {
      return dataOverride;
    }
    return buildData();
  }

  /**
   * Get the raw body text of the HTTP request, if available.
   */
  @Nullable
  public final String getBodyText() {
    String s = bodyText;
    if (s == null) {
      bodyText = s = InternalUtils.getBodyText(response);
    }
    return s;
  }

  protected abstract T buildData();

}
