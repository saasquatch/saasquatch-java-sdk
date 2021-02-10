package com.saasquatch.sdk.output;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.saasquatch.sdk.internal.json.GsonUtils;

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

  public int getStatusCode() {
    return statusCode;
  }

  @Nullable
  public String getRsCode() {
    return rsCode;
  }

  static ApiError fromJson(String bodyText, int statusCode) {
    /*
     * Doing this because in case of a catastrophic failure, we may not be able to get an actual
     * ApiError from SaaSquatch.
     */
    JsonElement jsonElement;
    try {
      jsonElement = JsonParser.parseString(bodyText);
    } catch (JsonParseException e) {
      jsonElement = null;
    }
    /*
     * If the response is a JSON object and it has the appropriate fields, then it's probably an
     * actual ApiError.
     */
    if (jsonElement instanceof JsonObject) {
      final JsonObject jsonObject = ((JsonObject) jsonElement);
      if (jsonObject.has("message") && jsonObject.has("statusCode")) {
        return GsonUtils.gson.fromJson(jsonObject, ApiError.class);
      }
    }
    /*
     * This is a catastrophic failure and SaaSquatch servers failed to respond with a proper
     * ApiError. Just jam the response text into the error message.
     */
    return new ApiError(bodyText, "UNHANDLED_API_EXCEPTION", statusCode, null);
  }

}
