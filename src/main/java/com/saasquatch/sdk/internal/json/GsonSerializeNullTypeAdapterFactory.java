package com.saasquatch.sdk.internal.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public enum GsonSerializeNullTypeAdapterFactory implements TypeAdapterFactory {

  INSTANCE;

  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    final Field[] declaredFields = type.getRawType().getDeclaredFields();
    final List<String> nullableFieldNames = new ArrayList<>();
    final List<String> nonNullFieldNames = new ArrayList<>();
    for (Field declaredField : declaredFields) {
      if (declaredField.getAnnotation(GsonSerializeNull.class) == null) {
        nonNullFieldNames.add(getSerializedName(declaredField));
      } else {
        nullableFieldNames.add(getSerializedName(declaredField));
      }
    }
    if (nullableFieldNames.isEmpty()) {
      return null;
    }
    final TypeAdapter<T> delegateAdapter = gson.getDelegateAdapter(
        GsonSerializeNullTypeAdapterFactory.INSTANCE, type);
    final TypeAdapter<JsonElement> elementAdapter = gson.getAdapter(JsonElement.class);
    return new TypeAdapter<T>() {

      @Override
      public void write(JsonWriter out, T value) throws IOException {
        final JsonObject jsonObject = delegateAdapter.toJsonTree(value).getAsJsonObject();
        for (String fieldName : nonNullFieldNames) {
          if (jsonObject.get(fieldName) instanceof JsonNull) {
            jsonObject.remove(fieldName);
          }
        }
        final boolean originalSerializeNulls = out.getSerializeNulls();
        out.setSerializeNulls(true);
        elementAdapter.write(out, jsonObject);
        out.setSerializeNulls(originalSerializeNulls);
      }

      @Override
      public T read(JsonReader in) throws IOException {
        return delegateAdapter.read(in);
      }

    };
  }

  private static String getSerializedName(Field field) {
    final SerializedName serializedName = field.getAnnotation(SerializedName.class);
    return serializedName == null ? field.getName() : serializedName.value();
  }

}
