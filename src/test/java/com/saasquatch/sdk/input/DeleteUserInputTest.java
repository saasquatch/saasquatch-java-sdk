package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DeleteUserInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> DeleteUserInput.newBuilder().build());
    assertThrows(NullPointerException.class,
        () -> DeleteUserInput.newBuilder().setUserId("a").build());
    assertThrows(NullPointerException.class,
        () -> DeleteUserInput.newBuilder().setAccountId("a").build());
    assertDoesNotThrow(() -> DeleteUserInput.newBuilder().setAccountId("a").setUserId("a").build());
  }

  @Test
  public void testBasic() {
    assertFalse(
        DeleteUserInput.newBuilder().setAccountId("a").setUserId("a").build().isDoNotTrack());
    assertTrue(
        DeleteUserInput.newBuilder().setAccountId("a").setUserId("a").setDoNotTrack(true).build()
            .isDoNotTrack());
  }

}
