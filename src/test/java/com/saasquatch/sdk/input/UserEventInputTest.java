package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class UserEventInputTest {

  @Test
  public void testValidation() {
    assertThrows(IllegalArgumentException.class, () -> UserEventInput.newBuilder().build());
    assertThrows(NullPointerException.class, () -> UserEventInput.newBuilder()
        .addEvents(UserEventDataInput.newBuilder().build()).build());
    assertThrows(NullPointerException.class, () -> UserEventInput.newBuilder().setAccountId("a")
        .setUserId("a").addEvents(UserEventDataInput.newBuilder().build()).build());
    assertThrows(NullPointerException.class, () -> UserEventInput.newBuilder()
        .addEvents(UserEventDataInput.newBuilder().setKey("foo").build()).build());
  }

}
