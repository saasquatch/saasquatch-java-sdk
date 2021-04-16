package com.saasquatch.sdk.input;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.Test;

public class WidgetTypeTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> WidgetType.ofConstant(null));
    assertThrows(IllegalArgumentException.class, () -> WidgetType.ofConstant(""));
    assertThrows(IllegalArgumentException.class, () -> WidgetType.ofProgramWidget("", ""));
    assertEquals("p/a%20b%20%26%2A~/w/%7B%3APG_%2B%24%23T%25%24%29",
        WidgetType.ofProgramWidget("a b &*~", "{:PG_+$#T%$)").getWidgetType());
    assertThrows(IllegalArgumentException.class, () -> WidgetType.ofGlobalWidget(""));
    assertEquals("w/%7B%3APG_%2B%24%23T%25%24%29",
        WidgetType.ofGlobalWidget("{:PG_+$#T%$)").getWidgetType());
  }

  @Test
  public void testConstant() {
    for (int i = 0; i < 10; i++) {
      final byte[] bytes = new byte[512];
      ThreadLocalRandom.current().nextBytes(bytes);
      final String s = new String(bytes, UTF_8);
      assertSame(s, WidgetType.ofConstant(s).getWidgetType());
    }
  }

  @Test
  public void testGetProgramId() {
    assertNull(WidgetType.ofConstant("foo").getProgramId());
    assertNull(WidgetType.ofConstant("p/test").getProgramId());
    assertEquals("foo", WidgetType.ofConstant("p/foo/w/a").getProgramId());
    assertEquals("$$$", WidgetType.ofConstant("p/%24%24%24/a").getProgramId());
    assertNull(WidgetType.ofGlobalWidget("foo").getProgramId());
    assertEquals("#$%", WidgetType.ofProgramWidget("#$%", "key").getProgramId());
  }

}
