package com.saasquatch.sdk;

import java.io.IOException;
import java.io.UncheckedIOException;
import okhttp3.Response;

/**
 * {@link SaaSquatchApiResponse} that returns plain text
 *
 * @author sli
 */
public class SaaSquatchTextResponse extends SaaSquatchApiResponse<String> {

  SaaSquatchTextResponse(Response response) {
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
