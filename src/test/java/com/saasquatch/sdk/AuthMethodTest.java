package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class AuthMethodTest {

  @Test
  public void testNull() {
    assertThrows(NullPointerException.class, () -> AuthMethod.ofApiKey(null));
    assertThrows(NullPointerException.class, () -> AuthMethod.ofJwt(null));
  }

}
