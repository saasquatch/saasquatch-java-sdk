package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.http.SaaSquatchHttpResponse;

/**
 * {@link ApiResponse} that does not have a body but may still have a {@link ApiError}.
 *
 * @author sli
 */
public final class StatusOnlyApiResponse extends ApiResponse<Void> {

  @Internal
  public StatusOnlyApiResponse(SaaSquatchHttpResponse httpResponse) {
    super(httpResponse);
  }

  @Override
  protected Void buildData() {
    return null;
  }

}
