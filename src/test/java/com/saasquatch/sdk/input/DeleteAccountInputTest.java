package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class DeleteAccountInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> DeleteAccountInput.newBuilder().build());
    assertDoesNotThrow(() -> DeleteAccountInput.newBuilder().setAccountId("a").build());
  }

  @Test
  public void testBasic() {
    assertFalse(
        DeleteAccountInput.newBuilder().setAccountId("a").build().isDoNotTrack());
    assertTrue(DeleteAccountInput.newBuilder().setAccountId("a").setDoNotTrack(true).build()
        .isDoNotTrack());
  }

}
