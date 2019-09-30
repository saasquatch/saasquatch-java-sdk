package com.saasquatch.sdk;

import java.io.IOException;
import okhttp3.Response;

/**
 * {@link ApiResponse} that returns plain text
 *
 * @author sli
 */
public class TextApiResponse extends ApiResponse<String> {

  TextApiResponse(Response response) {
    super(response);
  }

  @Override
  protected String buildData() {
    try {
      return response.body().string();
    } catch (IOException e) {
      throw new RuntimeException(e);
      // TODO switch to UncheckedIOException when Android fully supports Java 8
      // throw new UncheckedIOException(e);
    }
  }

}
