package com.saasquatch.sdk.internal.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class GsonUtil {

  public static final Gson gson =
      new GsonBuilder().serializeNulls().registerTypeAdapter(Date.class, DateMillisAdapter.INSTANCE)
          .addSerializationExclusionStrategy(GsonIgnoreExlusionStrategy.INSTANCE).create();

  private GsonUtil() {}

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
  public static String toJsonExcludingNullRootLevalFields(Object src) {
    final JsonElement jsonTree = gson.toJsonTree(src);
    final JsonObject jsonObject = (JsonObject) jsonTree;
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

  private static enum DateMillisAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    INSTANCE;

    @Override
    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(src.getTime());
    }

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      return new Date(json.getAsLong());
    }

  }

  private static enum GsonIgnoreExlusionStrategy implements ExclusionStrategy {

    INSTANCE;

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
      return f.getAnnotation(GsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
      return clazz.getAnnotation(GsonIgnore.class) != null;
    }

  }

}
