package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;

final class NoAuth extends AuthMethod {

  static final NoAuth INSTANCE = new NoAuth();

  public NoAuth() {}

  @Override
  public void mutateRequest(SimpleHttpRequest request) {}

}
