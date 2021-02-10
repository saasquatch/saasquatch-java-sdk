package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.annotations.NoExternalImpl;
import com.saasquatch.sdk.internal.InternalUtils;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.Header;

/**
 * Represents an API response from SaaSquatch. If the request has {@link #succeeded()}, then you
 * should <em>typically</em> be able to get the API result from {@link #getData()}. If the request
 * had {@link #failed()}, then you should <em>typically</em> be able to get a {@link ApiError} from
 * {@link #getApiError()}.
 *
 * @author sli
 */
@NoExternalImpl
public abstract class ApiResponse<T> {

  // Lazy init. Not part of the lazy init of data and error, since those depend on bodyText.
  private String bodyText;
  private final SimpleHttpResponse response;

  @Internal
  ApiResponse(@Nonnull SimpleHttpResponse response) {
    this.response = Objects.requireNonNull(response);
  }

  public final int getStatusCode() {
    return response.getCode();
  }

  public final boolean succeeded() {
    final int statusCode = getStatusCode();
    return statusCode >= 200 && statusCode < 300;
  }

  public final boolean failed() {
    return !succeeded();
  }

  @Nullable
  public final String getHeader(String headerName) {
    final Header header = response.getFirstHeader(headerName);
    return header == null ? null : header.getValue();
  }

  @Nullable
  public final T getData() {
    if (!succeeded()) {
      return null;
    }
    return buildData();
  }

  @Nullable
  public final ApiError getApiError() {
    if (succeeded()) {
      return null;
    }
    return buildApiError();
  }

  /**
   * Get the raw body text of the HTTP request, if available.
   */
  @Nullable
  public String getBodyText() {
    String s = bodyText;
    if (s == null) {
      bodyText = s = InternalUtils.getBodyText(response);
    }
    return s;
  }

  protected abstract T buildData();

  protected ApiError buildApiError() {
    return ApiError.fromJson(getBodyText(), getStatusCode());
  }

}
