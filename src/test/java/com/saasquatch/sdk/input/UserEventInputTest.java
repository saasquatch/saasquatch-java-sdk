package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Arrays;
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

  @Test
  public void testBasic() {
    final UserEventInput userEventInput = UserEventInput.newBuilder().setAccountId("a")
        .setUserId("u")
        .setEvents(Arrays.asList(UserEventDataInput.newBuilder().setKey("key").build())).build();
    assertEquals("a", userEventInput.getAccountId());
    assertEquals("u", userEventInput.getUserId());
    assertEquals(1, userEventInput.getEvents().size());
  }

}
