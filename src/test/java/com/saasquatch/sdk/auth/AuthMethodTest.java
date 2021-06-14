package com.saasquatch.sdk.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.http.HttpHeaders;
import org.junit.jupiter.api.Test;

public class AuthMethodTest {

  @SuppressWarnings("ConstantConditions")
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
    final SimpleRequestBuilder requestBuilder = SimpleRequestBuilder.get("http://example.com");
    {
      final AuthMethod basicAuth = AuthMethod.ofBasic("foo", "bar");
      basicAuth.mutateRequest(requestBuilder);
      assertEquals("Basic Zm9vOmJhcg==",
          requestBuilder.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
    {
      final AuthMethod bearerAuth = AuthMethod.ofBearer("foobar");
      bearerAuth.mutateRequest(requestBuilder);
      assertEquals("Bearer foobar",
          requestBuilder.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
    {
      final AuthMethod bearerAuth = AuthMethod.ofJwt("foobar2");
      bearerAuth.mutateRequest(requestBuilder);
      assertEquals("Bearer foobar2",
          requestBuilder.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());
    }
  }

}
