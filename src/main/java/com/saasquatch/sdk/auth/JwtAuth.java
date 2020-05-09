package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.HttpHeaders;

class JwtAuth extends AuthMethod {

  private final String jwt;

  JwtAuth(String jwt) {
    super(false);
    this.jwt = jwt;
  }

  @Override
  public void mutateRequest(SimpleHttpRequest request) {
    request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
  }

}
