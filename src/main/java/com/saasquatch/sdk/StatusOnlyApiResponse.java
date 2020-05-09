package com.saasquatch.sdk;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

/**
 * {@link ApiResponse} that does not have a body but may still have a
 * {@link ApiError}.
 *
 * @author sli
 */
public class StatusOnlyApiResponse extends ApiResponse<Void> {

  StatusOnlyApiResponse(SimpleHttpResponse response) {
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
