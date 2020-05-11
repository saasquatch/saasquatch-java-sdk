package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.internal.json.GsonUtils;

public class UserInputTest {

  @Test
  public void testCustomJson() {
    final UserInput userInput = UserInput.newBuilder().setAccountId("a").setId("a")
        .setCustomFields(Collections.singletonMap("foo", null)).build();
    final String jsonStr = GsonUtils.toJson(userInput);
    assertEquals("{\"accountId\":\"a\",\"id\":\"a\",\"customFields\":{\"foo\":null}}", jsonStr);
  }

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> UserInput.newBuilder().build());
    assertThrows(NullPointerException.class,
        () -> UserInput.newBuilder().setAccountId("a").build());
    assertThrows(NullPointerException.class, () -> UserInput.newBuilder().setId("a").build());
  }

}
