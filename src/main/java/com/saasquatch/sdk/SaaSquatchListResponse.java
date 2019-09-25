package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal._GsonHolder.gson;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import com.google.gson.reflect.TypeToken;
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

}
