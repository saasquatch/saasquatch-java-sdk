package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;

class NoAuth extends AuthMethod {

  static final NoAuth INSTANCE = new NoAuth();

  public NoAuth() {
    super(true);
  }

  @Override
  public void mutateRequest(SimpleHttpRequest request) {}

}
