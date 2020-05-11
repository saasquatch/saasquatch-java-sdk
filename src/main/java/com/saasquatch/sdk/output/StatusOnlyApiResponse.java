package com.saasquatch.sdk.output;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import com.saasquatch.sdk.annotations.Internal;

/**
 * {@link ApiResponse} that does not have a body but may still have a {@link ApiError}.
 *
 * @author sli
 */
public final class StatusOnlyApiResponse extends ApiResponse<Void> {

  @Internal
  public StatusOnlyApiResponse(SimpleHttpResponse response) {
    super(response);
  }

  /**
   * @deprecated always null
   */
  @Override
  @Deprecated
  public Void getData() {
    return null;
  }

  @Override
  protected Void buildData() {
    return null;
  }

}