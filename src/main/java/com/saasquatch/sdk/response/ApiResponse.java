package com.saasquatch.sdk.response;

import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.Header;

/**
 * Represents an API response from SaaSquatch. If the request has {@link #succeeded()}, then you
 * should <em>typically</em> be able to get the API result from {@link #getData()}. If the request
 * had {@link #failed()}, then you should <em>typically</em> be able to get a
 * {@link ApiError} from {@link #getApiError()}.
 *
 * @author sli
 */
public abstract class ApiResponse<T> {

  // Lazy init
  private T data;
  // Lazy init
  private ApiError apiError;
  protected final SimpleHttpResponse response;

  ApiResponse(SimpleHttpResponse response) {
    this.response = response;
  }

  public int getStatusCode() {
    return response.getCode();
  }

  public boolean succeeded() {
    final int statusCode = getStatusCode();
    return statusCode >= 200 && statusCode < 300;
  }

  public boolean failed() {
    return !succeeded();
  }

  @Nullable
  public String getHeader(String headerName) {
    final Header firstHeader = response.getFirstHeader(headerName);
    if (firstHeader == null) {
      return null;
    }
    return firstHeader.getValue();
  }

  @Nullable
  public T getData() {
    if (failed()) {
      return null;
    }
    T _data = data;
    if (_data == null) {
      data = _data = buildData();
    }
    return _data;
  }

  @Nullable
  public ApiError getApiError() {
    if (succeeded()) {
      return null;
    }
    ApiError _apiError = apiError;
    if (_apiError == null) {
      apiError = _apiError = ApiError.fromResponse(response);
    }
    return _apiError;
  }

  protected abstract T buildData();

}
