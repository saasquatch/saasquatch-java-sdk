package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class SaaSquatchClientTest {

  @Test
  public void testNull() {
    assertThrows(NullPointerException.class, () -> SaaSquatchClient.createForTenant(null));
    assertThrows(IllegalArgumentException.class, () -> SaaSquatchClient.createForTenant(" "));
    assertThrows(NullPointerException.class, () -> SaaSquatchClient.create(null));
    try (SaaSquatchClient saasquatchClient = SaaSquatchClient.createForTenant("fake")) {
      assertThrows(NullPointerException.class, () -> saasquatchClient.getUser(null, null, null));
      assertThrows(IllegalArgumentException.class, () -> saasquatchClient.getUser(" ", null, null));
      assertThrows(IllegalArgumentException.class, () -> saasquatchClient.getUser(" ", " ", null));
    }
  }

}
