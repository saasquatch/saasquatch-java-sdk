package com.saasquatch.sdk.output;

import static com.saasquatch.sdk.internal.InternalUtils.getBodyText;
import static com.saasquatch.sdk.internal.json.GsonUtil.gson;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

/**
 * The result of a standard GraphQL request
 *
 * @author sli
 */
public final class GraphQLResult {

  private final Map<String, Object> data;
  private final List<Object> errors;
  private final Map<String, Object> extensions;

  private GraphQLResult(Map<String, Object> data, List<Object> errors,
      Map<String, Object> extensions) {
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

  static GraphQLResult fromResponse(SimpleHttpResponse response) {
    return gson.fromJson(getBodyText(response), GraphQLResult.class);
  }

}
