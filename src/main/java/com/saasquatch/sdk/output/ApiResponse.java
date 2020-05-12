package com.saasquatch.sdk.output;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.Header;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.InternalUtils;

/**
 * Represents an API response from SaaSquatch. If the request has {@link #succeeded()}, then you
 * should <em>typically</em> be able to get the API result from {@link #getData()}. If the request
 * had {@link #failed()}, then you should <em>typically</em> be able to get a {@link ApiError} from
 * {@link #getApiError()}.
 *
 * @author sli
 */
public abstract class ApiResponse<T> {

  // Lazy init
  private T data;
  // Lazy init
  private ApiError apiError;
  private boolean fieldsInitialized;
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
  public T getData() {
    if (!succeeded()) {
      return null;
    }
    initializeFields();
    return this.data;
  }

  @Nullable
  public ApiError getApiError() {
    if (succeeded()) {
      return null;
    }
    initializeFields();
    return this.apiError;
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

  private void initializeFields() {
    if (!fieldsInitialized) {
      synchronized (response) {
        if (!fieldsInitialized) {
          if (succeeded()) {
            data = buildData();
          } else {
            apiError = ApiError.fromJson(getBodyText(), getStatusCode());
          }
          fieldsInitialized = true;
        }
      }
    }
  }

}
