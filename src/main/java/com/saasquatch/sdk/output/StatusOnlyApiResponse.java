package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.util.SaaSquatchHttpResponse;

/**
 * {@link ApiResponse} that does not have a body but may still have a {@link ApiError}.
 *
 * @author sli
 */
@Beta
public final class StatusOnlyApiResponse extends ApiResponse<Void> {

  @Internal
  public StatusOnlyApiResponse(SaaSquatchHttpResponse response) {
    super(response);
  }

  @Override
  protected Void buildData() {
    return null;
  }

}
