package com.saasquatch.sdk;

import okhttp3.Response;

/**
 * Response from a GraphQL request. Note that this class differs from other
 * {@link SaaSquatchApiResponse} implementations in that even if the request causes an error, the
 * response will still usually {@link #succeeded() succeed}. If the the request has actually
 * {@link #failed()}, then it's usually a catastrophic failure. To get the actual error (and data),
 * use {@link #getData()} and access it from {@link SaaSquatchGraphQLResult}.
 *
 * @author sli
 * @see SaaSquatchGraphQLResult
 */
public class SaaSquatchGraphQLResponse extends SaaSquatchApiResponse<SaaSquatchGraphQLResult> {

  SaaSquatchGraphQLResponse(Response response) {
    super(response);
  }

  @Override
  protected SaaSquatchGraphQLResult buildData() {
    return SaaSquatchGraphQLResult.fromResponse(response);
  }

}
