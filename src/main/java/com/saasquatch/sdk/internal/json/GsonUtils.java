package com.saasquatch.sdk.internal.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Date;

public final class GsonUtils {

  public static final Gson gson = new GsonBuilder()
      .registerTypeAdapterFactory(GsonSerializeNullTypeAdapterFactory.INSTANCE)
      .registerTypeAdapter(Date.class, DateMillisAdapter.INSTANCE)
      .addSerializationExclusionStrategy(GsonIgnoreExclusionStrategy.INSTANCE)
      .create();

  private GsonUtils() {}

}
