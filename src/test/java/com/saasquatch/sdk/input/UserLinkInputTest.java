package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class UserLinkInputTest {

  @Test
  public void testCustomJson() {
    final UserLinkInput userLinkInput =
        UserLinkInput.newBuilder().setAccountId("a").setUserId("a").build();
    assertThrows(UnsupportedOperationException.class, userLinkInput::toJsonString);
  }

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> UserLinkInput.newBuilder().build());
    assertThrows(NullPointerException.class,
        () -> UserLinkInput.newBuilder().setAccountId("a").build());
    assertThrows(NullPointerException.class,
        () -> UserLinkInput.newBuilder().setUserId("a").build());
  }

}
