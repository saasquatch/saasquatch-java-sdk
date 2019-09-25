package com.saasquatch.sdk;

import okhttp3.Response;

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
