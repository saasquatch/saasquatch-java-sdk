package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.http.HttpHeaders;

final class BearerAuth implements AuthMethod {

  private final String token;

  BearerAuth(String token) {
    this.token = token;
  }

  @Override
  public void mutateRequest(SimpleRequestBuilder requestBuilder) {
    requestBuilder.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
  }

}
