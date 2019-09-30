package com.saasquatch.sdk;

import java.io.IOException;
import java.io.UncheckedIOException;
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
      throw new UncheckedIOException(e);
    }
  }

}
