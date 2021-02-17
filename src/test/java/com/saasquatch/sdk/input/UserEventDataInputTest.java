package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class UserEventDataInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> UserEventDataInput.newBuilder().build());
    assertThrows(IllegalArgumentException.class,
        () -> UserEventDataInput.newBuilder().setKey(" ").build());
    assertDoesNotThrow(() -> UserEventDataInput.newBuilder().setKey("foo").build());
  }

  @Test
  public void testBasic() {
    final UserEventDataInput userEventDataInput = UserEventDataInput.newBuilder().setKey("key")
        .setDateTriggered(new Date(123)).setFields(Collections.singletonMap("foo", "bar")).build();
    assertEquals("key", userEventDataInput.getKey());
    assertEquals(new Date(123), userEventDataInput.getDateTriggered());
    assertEquals(Collections.singletonMap("foo", "bar"), userEventDataInput.getFields());
  }

}
