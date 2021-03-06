package com.saasquatch.sdk.auth;

import static java.nio.charset.StandardCharsets.UTF_8;
import org.apache.commons.codec.binary.Base64;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.http.HttpHeaders;

final class BasicAuth implements AuthMethod {

  private final String username;
  private final String password;

  BasicAuth(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void mutateRequest(SimpleRequestBuilder requestBuilder) {
    requestBuilder.setHeader(HttpHeaders.AUTHORIZATION,
        "Basic " + Base64.encodeBase64String((username + ':' + password).getBytes(UTF_8)));
  }

}
