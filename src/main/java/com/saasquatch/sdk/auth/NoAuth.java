package com.saasquatch.sdk.auth;

import javax.annotation.concurrent.Immutable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;

@Immutable
class NoAuth extends AuthMethod {

  static final NoAuth INSTANCE = new NoAuth();

  public NoAuth() {
    super(true);
  }

  @Override
  public void mutateRequest(SimpleHttpRequest request) {}

}
