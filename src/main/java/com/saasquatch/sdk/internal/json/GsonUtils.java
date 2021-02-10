package com.saasquatch.sdk.internal.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class GsonUtils {

  public static final Gson gson =
      new GsonBuilder().serializeNulls().registerTypeAdapter(Date.class, DateMillisAdapter.INSTANCE)
          .addSerializationExclusionStrategy(GsonIgnoreExlusionStrategy.INSTANCE).create();

  private GsonUtils() {}

  public static String toJson(Object src) {
    if (src instanceof HasCustomJsonSerialization) {
      return ((HasCustomJsonSerialization) src).toJsonString();
    }
    return gson.toJson(src);
  }

  /**
   * Serialize the object to JSON while excluding null root level fields. Only works if the input
   * object is to be serialized into a JSON object.
   */
  public static String toJsonExcludingNullRootLevelFields(Object src) {
    final JsonObject jsonObject = (JsonObject) gson.toJsonTree(src);
    final List<String> fieldsToRemove = new ArrayList<>(jsonObject.size());
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      if (entry.getValue() == null || entry.getValue().isJsonNull()) {
        fieldsToRemove.add(entry.getKey());
      }
    }
    for (String field : fieldsToRemove) {
      jsonObject.remove(field);
    }
    return jsonObject.toString();
  }

}
