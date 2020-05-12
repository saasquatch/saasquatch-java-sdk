package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.auth.AuthMethods;

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

}
