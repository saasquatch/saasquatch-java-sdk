package saasquatch.sdk;

import okhttp3.Response;

public class SaaSquatchGraphQLResponse extends SaaSquatchApiResponse<SaaSquatchGraphQLResult> {

  SaaSquatchGraphQLResponse(Response response) {
    super(response);
  }

  @Override
  protected SaaSquatchGraphQLResult buildData() {
    return SaaSquatchGraphQLResult.fromResponse(response);
  }

}
