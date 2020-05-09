package com.saasquatch.sdk.output;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import com.saasquatch.sdk.annotations.Internal;

/**
 * Response from a GraphQL request. Note that this class differs from other {@link ApiResponse}
 * implementations in that even if the request causes an error, the response will still usually
 * {@link #succeeded() succeed}. If the the request has actually {@link #failed()}, then it's
 * usually a catastrophic failure. To get the actual error (and data), use {@link #getData()} and
 * access it from {@link GraphQLResult}.
 *
 * @author sli
 * @see GraphQLResult
 */
public final class GraphQLApiResponse extends ApiResponse<GraphQLResult> {

  @Internal
  public GraphQLApiResponse(SimpleHttpResponse response) {
    super(response);
  }

  @Override
  protected GraphQLResult buildData() {
    return GraphQLResult.fromResponse(response);
  }

}
