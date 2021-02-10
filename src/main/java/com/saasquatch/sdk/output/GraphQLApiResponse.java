package com.saasquatch.sdk.output;

import com.saasquatch.sdk.util.SaaSquatchHttpResponse;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.json.GsonUtils;

/**
 * Response from a GraphQL request. Note that this class differs from other {@link ApiResponse}
 * implementations in that even if the request causes an error, the response will still usually
 * succeed. If the the request has actually failed, then it's usually a catastrophic failure. To get
 * the actual error (and data), use {@link #getData()} and access it from {@link GraphQLResult}.
 *
 * @author sli
 * @see GraphQLResult
 */
public final class GraphQLApiResponse extends ApiResponse<GraphQLResult> {

  @Internal
  public GraphQLApiResponse(SaaSquatchHttpResponse response) {
    super(response);
  }

  @Override
  protected GraphQLResult buildData() {
    return GsonUtils.gson.fromJson(getHttpResponse().getBodyText(), GraphQLResult.class);
  }

}
