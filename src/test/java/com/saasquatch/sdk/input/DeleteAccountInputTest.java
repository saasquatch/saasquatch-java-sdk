package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class DeleteAccountInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> DeleteAccountInput.newBuilder().build());
    assertDoesNotThrow(() -> DeleteAccountInput.newBuilder().setAccountId("a").build());
  }

  @Test
  public void testBasic() {
    assertNull(DeleteAccountInput.newBuilder().setAccountId("a").build().getDoNotTrack());
    assertEquals(true, DeleteAccountInput.newBuilder().setAccountId("a").setDoNotTrack(true).build()
        .getDoNotTrack());
  }

}
