package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;

enum NoAuth implements AuthMethod {

  INSTANCE;

  @Override
  public void mutateRequest(SimpleHttpRequest request) {}

}
