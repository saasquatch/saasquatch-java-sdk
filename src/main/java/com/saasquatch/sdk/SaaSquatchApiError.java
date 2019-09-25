package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import java.io.IOException;
import okhttp3.Response;

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

  public String getMessage() {
    return message;
  }

  public String getApiErrorCode() {
    return apiErrorCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

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
