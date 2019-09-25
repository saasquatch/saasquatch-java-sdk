package com.saasquatch.sdk;

import okhttp3.Response;

/**
 * {@link SaaSquatchApiResponse} that does not have a body but may still have a
 * {@link SaaSquatchApiError}.
 *
 * @author sli
 */
public class SaaSquatchStatusOnlyResponse extends SaaSquatchApiResponse<Void> {

  SaaSquatchStatusOnlyResponse(Response response) {
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
