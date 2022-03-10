package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    assertNull(
        DeleteUserInput.newBuilder().setAccountId("a").setUserId("a").build().getDoNotTrack());
    assertEquals(true,
        DeleteUserInput.newBuilder().setAccountId("a").setUserId("a").setDoNotTrack(true).build()
            .getDoNotTrack());
    assertEquals(true,
        DeleteUserInput.newBuilder().setAccountId("a").setUserId("a").setPreserveEmptyAccount(true)
            .build()
            .getPreserveEmptyAccount());
  }

}
