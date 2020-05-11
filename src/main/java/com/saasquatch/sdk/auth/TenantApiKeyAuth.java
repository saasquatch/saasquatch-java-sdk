package com.saasquatch.sdk.auth;

import static java.nio.charset.StandardCharsets.UTF_8;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.HttpHeaders;
import com.saasquatch.sdk.internal.OkioBase64;

class TenantApiKeyAuth extends AuthMethod {

  private final String apiKey;

  TenantApiKeyAuth(String apiKey) {
    super(true);
    this.apiKey = apiKey;
  }

  @Override
  public void mutateRequest(SimpleHttpRequest request) {
    request.setHeader(HttpHeaders.AUTHORIZATION,
        "Basic " + OkioBase64.encode((":" + apiKey).getBytes(UTF_8)));
  }

}