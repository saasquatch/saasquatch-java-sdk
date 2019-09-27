package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import okhttp3.Response;

/**
 * The result of a standard GraphQL request
 *
 * @author sli
 */
public class SaaSquatchGraphQLResult {

  private final Map<String, Object> data;
  private final List<Object> errors;
  private final Map<String, Object> extensions;

  SaaSquatchGraphQLResult(@Nullable Map<String, Object> data, @Nullable List<Object> errors,
      @Nullable Map<String, Object> extensions) {
    this.data = data;
    this.errors = errors;
    this.extensions = extensions;
  }

  @Nullable
  public Map<String, Object> getData() {
    return data;
  }

  @Nullable
  public List<Object> getErrors() {
    return errors;
  }

  @Nullable
  public Map<String, Object> getExtensions() {
    return extensions;
  }

  static SaaSquatchGraphQLResult fromResponse(Response response) {
    try {
      return gson.fromJson(response.body().string(), SaaSquatchGraphQLResult.class);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
