package com.saasquatch.sdk.output;

import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

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

}
