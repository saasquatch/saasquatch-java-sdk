package com.saasquatch.sdk.auth;

import javax.annotation.concurrent.Immutable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;

@Immutable
class JwtAuth extends AuthMethod {

  private final String jwt;

  JwtAuth(String jwt) {
    super(false);
    this.jwt = jwt;
  }

  @Override
  public void mutateRequest(SimpleHttpRequest request) {
    request.setHeader("Authorization", "Bearer " + jwt);
  }

}
