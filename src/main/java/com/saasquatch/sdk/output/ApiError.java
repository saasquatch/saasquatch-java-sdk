package com.saasquatch.sdk.output;

import static com.saasquatch.sdk.internal.InternalGsonHolder.gson;
import static com.saasquatch.sdk.internal.InternalUtils.getBodyText;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

/**
 * Represents an API error from SaaSquatch
 *
 * @author sli
 */
public final class ApiError {

  private final String message;
  private final String apiErrorCode;
  private final int statusCode;
  private final String rsCode;

  private ApiError(String message, String apiErrorCode, int statusCode, String rsCode) {
    this.message = message;
    this.apiErrorCode = apiErrorCode;
    this.statusCode = statusCode;
    this.rsCode = rsCode;
  }

  @Nonnull
  public String getMessage() {
    return message;
  }

  @Nonnull
  public String getApiErrorCode() {
    return apiErrorCode;
  }

  @Nonnull
  public int getStatusCode() {
    return statusCode;
  }

  @Nullable
  public String getRsCode() {
    return rsCode;
  }

  static ApiError fromResponse(SimpleHttpResponse response) {
    return gson.fromJson(getBodyText(response), ApiError.class);
  }

}
