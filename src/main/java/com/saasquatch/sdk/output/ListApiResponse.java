package com.saasquatch.sdk.output;

import static com.saasquatch.sdk.internal.InternalGsonHolder.gson;
import static com.saasquatch.sdk.internal.InternalUtils.format;
import static com.saasquatch.sdk.internal.InternalUtils.getBodyText;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import com.google.gson.reflect.TypeToken;
import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.models.Model;

/**
 * {@link ApiResponse} that has a JSON array. The JSON array will be represented as a {@link List}
 * since we want to be JSON library agnostic.
 *
 * @author sli
 */
@Beta
public final class ListApiResponse extends ApiResponse<List<Object>> {

  private ListApiResponse(SimpleHttpResponse response) {
    super(response);
  }

  @Override
  protected List<Object> buildData() {
    return gson.fromJson(getBodyText(response), new TypeToken<List<Object>>() {}.getType());
  }

  /**
   * Attempt to unmarshal this JSON response as a list of models. Note that this method only makes
   * sense when you are getting the default JSON response from SaaSquatch, i.e. you are not
   * customizing the JSON response body with things like {@code extraFields}. Otherwise it may cause
   * unexpected behaviors.
   */
  public <T extends Model> List<T> toModelList(@Nonnull Class<? extends T> modelClass) {
    Objects.requireNonNull(modelClass, "modelClass");
    /*
     * I know modelClass is technically not being used here, but it's included to be JSON library
     * agnostic
     */
    final List<T> modelList =
        gson.fromJson(gson.toJsonTree(getData()), new TypeToken<List<T>>() {}.getType());
    if (modelList == null) {
      throw new IllegalStateException(
          format("Unable to convert to model list with class [%s]", modelClass));
    }
    return Collections.unmodifiableList(modelList);
  }

}
