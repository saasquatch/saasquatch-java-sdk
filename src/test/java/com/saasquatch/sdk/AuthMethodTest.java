package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.auth.AuthMethods;

public class AuthMethodTest {

  @Test
  public void testNull() {
    assertThrows(NullPointerException.class, () -> AuthMethods.ofTenantApiKey(null));
    assertThrows(NullPointerException.class, () -> AuthMethods.ofJwt(null));
  }

  @Test
  public void testNoAuthSingleton() {
    assertSame(AuthMethods.noAuth(), AuthMethods.noAuth());
  }

}
