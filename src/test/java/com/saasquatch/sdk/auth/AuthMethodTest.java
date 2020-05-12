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
    assertThrows(NullPointerException.class, () -> AuthMethods.ofTenantApiKey(null));
    assertThrows(IllegalArgumentException.class, () -> AuthMethods.ofTenantApiKey(" "));
    assertThrows(NullPointerException.class, () -> AuthMethods.ofBasic(null, "abc"));
    assertThrows(NullPointerException.class, () -> AuthMethods.ofBasic("abc", null));
    assertDoesNotThrow(() -> AuthMethods.ofBasic("", ""));
    assertThrows(NullPointerException.class, () -> AuthMethods.ofJwt(null));
    assertThrows(IllegalArgumentException.class, () -> AuthMethods.ofJwt(" "));
    assertThrows(NullPointerException.class, () -> AuthMethods.ofBearer(null));
    assertThrows(IllegalArgumentException.class, () -> AuthMethods.ofBearer(" "));
  }

  @Test
  public void testNoAuthSingleton() {
    assertSame(AuthMethods.noAuth(), AuthMethods.noAuth());
  }

  @Test
  public void testImpl() {
    final SimpleHttpRequest request = SimpleHttpRequests.get("http://example.com");
    {
      final AuthMethod basicAuth = AuthMethods.ofBasic("foo", "bar");
      basicAuth.mutateRequest(request);
      assertEquals("Basic Zm9vOmJhcg==",
          request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
    {
      final AuthMethod bearerAuth = AuthMethods.ofBearer("foobar");
      bearerAuth.mutateRequest(request);
      assertEquals("Bearer foobar", request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
    {
      final AuthMethod bearerAuth = AuthMethods.ofJwt("foobar2");
      bearerAuth.mutateRequest(request);
      assertEquals("Bearer foobar2", request.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
  }

}
