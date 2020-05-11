package com.saasquatch.sdk.output;

import static com.saasquatch.sdk.internal.InternalUtils.getBodyText;
import static com.saasquatch.sdk.internal.json.GsonUtil.gson;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    /*
     * Doing this because in case of a catastrophic failure, we may not be able to get an actual
     * ApiError from SaaSquatch.
     */
    final String bodyText = getBodyText(response);
    final JsonElement jsonElement = JsonParser.parseString(bodyText);
    /*
     * If the response is a JSON object and it has the appropriate fields, then it's probably an
     * actual ApiError.
     */
    if (jsonElement instanceof JsonObject) {
      final JsonObject jsonObject = ((JsonObject) jsonElement);
      if (jsonObject.has("message") && jsonObject.has("statusCode")) {
        return gson.fromJson(jsonObject, ApiError.class);
      }
    }
    /*
     * This is a catastrophic failure and SaaSquatch servers failed to respond with a proper
     * ApiError. Just jam the response text into the error message.
     */
    return new ApiError(bodyText, "UNHANDLED_API_EXCEPTION", response.getCode(), null);
  }

}
