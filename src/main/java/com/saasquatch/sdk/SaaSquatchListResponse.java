package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import com.google.gson.reflect.TypeToken;
import com.saasquatch.sdk.models.SaaSquatchModel;
import okhttp3.Response;

/**
 * {@link SaaSquatchApiResponse} that has a JSON array. The JSON array will be represented as a
 * {@link List} since we want to be JSON library agnostic.
 *
 * @author sli
 */
public class SaaSquatchListResponse extends SaaSquatchApiResponse<List<Object>> {

  SaaSquatchListResponse(Response response) {
    super(response);
  }

  @Override
  protected List<Object> buildData() {
    try {
      return gson.fromJson(response.body().string(), new TypeToken<List<Object>>() {}.getType());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Attempt to unmarshal this JSON response as a list of models. Note that this method only makes
   * sense when you are getting the default JSON response from SaaSquatch, i.e. you are not
   * customizing the JSON response body with things like {@code extraFields}. Otherwise it may cause
   * unexpected behaviors.
   */
  public <T extends SaaSquatchModel> List<T> toModelList(@Nonnull Class<? extends T> modelClass) {
    Objects.requireNonNull(modelClass, "modelClass");
    /*
     * I know modelClass is technically not being used here, but it's included to be JSON library
     * agnostic
     */
    return gson.fromJson(gson.toJsonTree(getData()), new TypeToken<List<T>>() {}.getType());
  }

}
