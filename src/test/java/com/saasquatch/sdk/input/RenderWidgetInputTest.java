package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RenderWidgetInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class,
        () -> RenderWidgetInput.newBuilder().setWidgetType(null));
    assertThrows(NullPointerException.class,
        () -> RenderWidgetInput.newBuilder().setUser(null));
    assertThrows(NullPointerException.class,
        () -> RenderWidgetInput.newBuilder().setUserWithUserJwt(null));
    assertThrows(IllegalArgumentException.class,
        () -> RenderWidgetInput.newBuilder().setUserWithUserJwt(""));
    assertThrows(IllegalArgumentException.class,
        () -> RenderWidgetInput.newBuilder().setUserWithUserJwt("foo"));
    assertThrows(NullPointerException.class,
        () -> RenderWidgetInput.newBuilder().setEngagementMedium(null));
    assertThrows(IllegalArgumentException.class,
        () -> RenderWidgetInput.newBuilder().setEngagementMedium(""));
    assertDoesNotThrow(() -> RenderWidgetInput.newBuilder().setEngagementMedium("invalid"));
    assertThrows(NullPointerException.class,
        () -> RenderWidgetInput.newBuilder().setLocale(null));
    assertThrows(IllegalArgumentException.class,
        () -> RenderWidgetInput.newBuilder().setLocale(""));
    assertDoesNotThrow(() -> RenderWidgetInput.newBuilder().setLocale("invalid"));
  }

}
