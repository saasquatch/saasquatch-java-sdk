package com.saasquatch.sdk;

import okhttp3.Response;

/**
 * {@link ApiResponse} that does not have a body but may still have a
 * {@link ApiError}.
 *
 * @author sli
 */
public class StatusOnlyApiResponse extends ApiResponse<Void> {

  StatusOnlyApiResponse(Response response) {
    super(response);
  }

  @Override
  public Void getData() {
    return null;
  }

  @Override
  protected Void buildData() {
    return null;
  }

}
