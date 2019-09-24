package saasquatch.sdk;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import com.google.gson.reflect.TypeToken;
import okhttp3.Response;

public class SaaSquatchObjectResponse extends SaaSquatchApiResponse<Map<String, Object>> {

  SaaSquatchObjectResponse(Response response) {
    super(response);
  }

  @Override
  protected Map<String, Object> buildData() {
    try {
      return SaaSquatchClient.gson.fromJson(response.body().string(),
          new TypeToken<Map<String, Object>>() {}.getType());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
