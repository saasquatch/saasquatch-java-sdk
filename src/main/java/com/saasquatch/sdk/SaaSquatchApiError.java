package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import java.io.IOException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import okhttp3.Response;

/**
 * Represents an API error from SaaSquatch
 *
 * @author sli
 */
public class SaaSquatchApiError {

  private final String message;
  private final String apiErrorCode;
  private final int statusCode;
  private final String rsCode;

  SaaSquatchApiError(String message, String apiErrorCode, int statusCode, String rsCode) {
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

  static SaaSquatchApiError fromResponse(Response response) {
    try {
      return gson.fromJson(response.body().string(), SaaSquatchApiError.class);
    } catch (IOException e) {
      // Ignore on purpose
    }
    return null;
  }

}
