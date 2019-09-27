package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import static java.nio.charset.StandardCharsets.UTF_8;
import okhttp3.MediaType;
import okhttp3.RequestBody;

class InternalRequestBodies {

  private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

  public static RequestBody jsonPojo(Object bodyObj) {
    return jsonString(gson.toJson(bodyObj));
  }

  public static RequestBody jsonString(String jsonString) {
    return jsonBytes(jsonString.getBytes(UTF_8));
  }

  public static RequestBody jsonBytes(byte[] jsonBytes) {
    return RequestBody.create(jsonBytes, JSON_MEDIA_TYPE);
  }

}
