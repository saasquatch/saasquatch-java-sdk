package saasquatch.sdk;

import java.io.IOException;
import java.io.UncheckedIOException;
import okhttp3.Response;

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
