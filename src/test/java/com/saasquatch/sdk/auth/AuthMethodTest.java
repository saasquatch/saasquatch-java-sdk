package com.saasquatch.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.Test;

public class AuthMethodTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> AuthMethod.ofTenantApiKey(null));
    assertThrows(IllegalArgumentException.class, () -> AuthMethod.ofTenantApiKey(" "));
    assertThrows(NullPointerException.class, () -> AuthMethod.ofBasic(null, "abc"));
    assertThrows(NullPointerException.class, () -> AuthMethod.ofBasic("abc", null));
    assertDoesNotThrow(() -> AuthMethod.ofBasic("", ""));
    assertThrows(NullPointerException.class, () -> AuthMethod.ofJwt(null));
    assertThrows(IllegalArgumentException.class, () -> AuthMethod.ofJwt(" "));
    assertThrows(NullPointerException.class, () -> AuthMethod.ofBearer(null));
    assertThrows(IllegalArgumentException.class, () -> AuthMethod.ofBearer(" "));
  }

  @Test
  public void testNoAuthSingleton() {
    assertSame(AuthMethod.noAuth(), AuthMethod.noAuth());
  }

  @Test
  public void testImpl() {
    final SimpleHttpRequest request = SimpleHttpRequests.get("http://example.com");
    {
      final AuthMethod basicAuth = AuthMethod.ofBasic("foo", "bar");
      basicAuth.mutateRequest(request);
      assertEquals("Basic Zm9vOmJhcg==",
          request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
    {
      final AuthMethod bearerAuth = AuthMethod.ofBearer("foobar");
      bearerAuth.mutateRequest(request);
      assertEquals("Bearer foobar", request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
    {
      final AuthMethod bearerAuth = AuthMethod.ofJwt("foobar2");
      bearerAuth.mutateRequest(request);
      assertEquals("Bearer foobar2", request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
  }

}
