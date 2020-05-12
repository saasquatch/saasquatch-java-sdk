package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class UserEventDataInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> UserEventDataInput.newBuilder().build());
    assertThrows(IllegalArgumentException.class,
        () -> UserEventDataInput.newBuilder().setKey(" ").build());
    assertDoesNotThrow(() -> UserEventDataInput.newBuilder().setKey("foo").build());
  }

}
