package com.saasquatch.sdk.auth;

import static java.nio.charset.StandardCharsets.UTF_8;
import javax.annotation.concurrent.Immutable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import com.saasquatch.sdk.internal.OkioBase64;

@Immutable
class TenantApiKeyAuth extends AuthMethod {

  private final String apiKey;

  TenantApiKeyAuth(String apiKey) {
    super(true);
    this.apiKey = apiKey;
  }

  @Override
  public void mutateRequest(SimpleHttpRequest request) {
    request.setHeader("Authorization",
        "Basic " + OkioBase64.encode((":" + apiKey).getBytes(UTF_8)));
  }

}
