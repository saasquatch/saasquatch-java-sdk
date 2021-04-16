package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class ApplyReferralCodeInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> ApplyReferralCodeInput.newBuilder().build());
    assertThrows(NullPointerException.class,
        () -> ApplyReferralCodeInput.newBuilder().setUserId("a").build());
    assertThrows(NullPointerException.class,
        () -> ApplyReferralCodeInput.newBuilder().setUserId("a").setAccountId("a").build());
    assertDoesNotThrow(() -> ApplyReferralCodeInput.newBuilder().setUserId("a").setAccountId("a")
        .setReferralCode("a").build());
    assertThrows(IllegalArgumentException.class,
        () -> ApplyReferralCodeInput.newBuilder().setUserId("").build());
    assertThrows(IllegalArgumentException.class,
        () -> ApplyReferralCodeInput.newBuilder().setAccountId("").build());
    assertThrows(IllegalArgumentException.class,
        () -> ApplyReferralCodeInput.newBuilder().setReferralCode("").build());
  }

}
