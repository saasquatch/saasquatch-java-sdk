package com.saasquatch.sdk;

import javax.annotation.Nullable;
import okhttp3.Response;

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
  protected final Response response;

  ApiResponse(Response response) {
    this.response = response;
  }

  public int getStatusCode() {
    return response.code();
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
    return response.header(headerName);
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
