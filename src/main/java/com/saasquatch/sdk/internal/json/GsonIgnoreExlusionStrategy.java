package com.saasquatch.sdk.internal.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public enum GsonIgnoreExlusionStrategy implements ExclusionStrategy {

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
