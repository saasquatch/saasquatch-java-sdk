package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;

enum NoAuth implements AuthMethod {

  INSTANCE;

  @Override
  public void mutateRequest(SimpleRequestBuilder requestBuilder) {}

}
