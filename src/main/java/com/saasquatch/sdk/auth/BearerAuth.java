package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.HttpHeaders;

final class BearerAuth extends AuthMethod {

  private final String token;

  BearerAuth(String token) {
    this.token = token;
  }

  @Override
  public void mutateRequest(SimpleHttpRequest request) {
    request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
  }

}
