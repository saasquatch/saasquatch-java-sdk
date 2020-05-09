package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.saasquatch.sdk.SaaSquatchClient;

/**
 * Input of a GraphQL request
 *
 * @author sli
 * @see #newBuilder()
 * @see SaaSquatchClient#graphQL(GraphQLInput, com.saasquatch.sdk.RequestOptions)
 */
public final class GraphQLInput {

  private final String query;
  private final String operationName;
  private final Map<String, Object> variables;

  private GraphQLInput(@Nonnull String query, @Nullable String operationName,
      @Nullable Map<String, Object> variables) {
    this.query = query;
    this.operationName = operationName;
    this.variables = variables;
  }

  public String getQuery() {
    return query;
  }

  public String getOperationName() {
    return operationName;
  }

  public Map<String, Object> getVariables() {
    return variables;
  }

  public static GraphQLInput ofQuery(String query) {
    return newBuilder().setQuery(query).build();
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String query;
    private String operationName;
    private Map<String, Object> variables;

    private Builder() {}

    public Builder setQuery(@Nonnull String query) {
      this.query = requireNotBlank(query, "query");
      return this;
    }

    public Builder setOperationName(@Nonnull String operationName) {
      this.operationName = requireNotBlank(operationName, "operationName");
      return this;
    }

    public Builder setVariables(@Nonnull Map<String, Object> variables) {
      this.variables = Objects.requireNonNull(variables, "variables");
      return this;
    }

    public GraphQLInput build() {
      return new GraphQLInput(requireNotBlank(query, "query"), operationName,
          variables == null ? null : Collections.unmodifiableMap(new HashMap<>(variables)));
    }

  }

}
