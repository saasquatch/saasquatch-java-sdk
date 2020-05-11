package com.saasquatch.sdk.internal;

import java.lang.reflect.Type;
import java.util.Date;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public final class InternalGsonHolder {

  private InternalGsonHolder() {}

  public static final Gson gson =
      new GsonBuilder().serializeNulls().registerTypeAdapter(Date.class, DateMillisAdapter.INSTANCE)
          .addSerializationExclusionStrategy(GsonIgnoreExlusionStrategy.INSTANCE).create();

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
