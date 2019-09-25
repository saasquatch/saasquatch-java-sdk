package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import okhttp3.Response;

/**
 * {@link SaaSquatchApiResponse} that has a JSON object. The JSON object will be represented as a
 * {@link Map} since we want to be JSON library agnostic.
 *
 * @author sli
 */
public class SaaSquatchMapResponse extends SaaSquatchApiResponse<Map<String, Object>> {

  SaaSquatchMapResponse(Response response) {
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

}
