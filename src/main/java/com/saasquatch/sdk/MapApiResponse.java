package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import com.google.gson.reflect.TypeToken;
import com.saasquatch.sdk.models.SaaSquatchModel;
import okhttp3.Response;

/**
 * {@link ApiResponse} that has a JSON object. The JSON object will be represented as a {@link Map}
 * since we want to be JSON library agnostic.
 *
 * @author sli
 */
public class MapApiResponse extends ApiResponse<Map<String, Object>> {

  MapApiResponse(Response response) {
    super(response);
  }

  @Override
  protected Map<String, Object> buildData() {
    try {
      return gson.fromJson(response.body().string(),
          new TypeToken<Map<String, Object>>() {}.getType());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Attempt to unmarshal this JSON response as a model class. Note that this method only makes
   * sense when you are getting the default JSON response from SaaSquatch, i.e. you are not
   * customizing the JSON response body with things like {@code extraFields}. Otherwise it may cause
   * unexpected behaviors.
   */
  public <T extends SaaSquatchModel> T toModel(@Nonnull Class<? extends T> modelClass) {
    Objects.requireNonNull(modelClass, "modelClass");
    final T model = gson.fromJson(gson.toJsonTree(getData()), modelClass);
    if (model == null) {
      throw new IllegalStateException(
          String.format("Unable to convert to model with class [%s]", modelClass));
    }
    return model;
  }

}
