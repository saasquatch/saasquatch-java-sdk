package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class WidgetTypeTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> WidgetType.of(null));
    assertThrows(IllegalArgumentException.class, () -> WidgetType.of(""));
    assertThrows(IllegalArgumentException.class, () -> WidgetType.ofProgramWidget("", ""));
    assertEquals("p/a%20b%20%26%2A~/w/%7B%3APG_%2B%24%23T%25%24%29",
        WidgetType.ofProgramWidget("a b &*~", "{:PG_+$#T%$)").getWidgetType());
  }

}
