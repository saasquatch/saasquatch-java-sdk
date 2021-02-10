package com.saasquatch.sdk.output;

import static com.saasquatch.sdk.internal.InternalUtils.format;

import com.google.gson.reflect.TypeToken;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.json.GsonUtils;
import com.saasquatch.sdk.models.Model;
import com.saasquatch.sdk.util.SaaSquatchHttpResponse;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * {@link ApiResponse} that has a JSON object. The JSON object will be represented as a {@link Map}
 * since we want to be JSON library agnostic.
 *
 * @author sli
 */
public final class JsonObjectApiResponse extends ApiResponse<Map<String, Object>> {

  @Internal
  public JsonObjectApiResponse(SaaSquatchHttpResponse response) {
    super(response);
  }

  @Override
  protected Map<String, Object> buildData() {
    return GsonUtils.gson.fromJson(getHttpResponse().getBodyText(),
        new TypeToken<Map<String, Object>>() {}.getType());
  }

  /**
   * Attempt to unmarshal this JSON response as a model class. Note that this method only makes
   * sense when you are getting the default JSON response from SaaSquatch, i.e. you are not
   * customizing the JSON response body with things like {@code extraFields}. Otherwise it may cause
   * unexpected behaviors.
   */
  public <T extends Model> T toModel(@Nonnull Class<? extends T> modelClass) {
    Objects.requireNonNull(modelClass, "modelClass");
    final T model = GsonUtils.gson.fromJson(GsonUtils.gson.toJsonTree(getData()), modelClass);
    if (model == null) {
      throw new IllegalStateException(
          format("Unable to convert to model with class [%s]", modelClass));
    }
    return model;
  }

}
