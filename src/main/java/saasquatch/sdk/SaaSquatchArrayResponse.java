package saasquatch.sdk;

import static saasquatch.sdk.SaaSquatchClient.gson;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import okhttp3.Response;

public class SaaSquatchArrayResponse extends SaaSquatchApiResponse<List<Object>> {

  SaaSquatchArrayResponse(Response response) {
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
