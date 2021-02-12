package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.saasquatch.sdk.input.WidgetUpsertInput;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import com.google.common.collect.ImmutableMap;
import com.saasquatch.sdk.input.UserInput;

public class SaaSquatchClientTest {

  @Test
  public void testNull() {
    assertThrows(NullPointerException.class, () -> SaaSquatchClient.createForTenant(null));
    assertThrows(IllegalArgumentException.class, () -> SaaSquatchClient.createForTenant(" "));
    assertThrows(NullPointerException.class, () -> SaaSquatchClient.create(null));
    try (SaaSquatchClient saasquatchClient = SaaSquatchClient.createForTenant("fake")) {
      assertThrows(NullPointerException.class, () -> saasquatchClient.getUser(null, null, null));
      assertThrows(IllegalArgumentException.class, () -> saasquatchClient.getUser(" ", null, null));
      assertThrows(IllegalArgumentException.class, () -> saasquatchClient.getUser(" ", " ", null));
      assertThrows(NullPointerException.class,
          () -> saasquatchClient.renderWidget(null, null));
      assertThrows(NullPointerException.class,
          () -> saasquatchClient.userUpsert((UserInput) null, null));
      assertThrows(NullPointerException.class,
          () -> saasquatchClient.userUpsert(Collections.emptyMap(), null));
      assertThrows(IllegalArgumentException.class,
          () -> saasquatchClient.userUpsert(Collections.singletonMap("accountId", " "), null));
      assertThrows(IllegalArgumentException.class,
          () -> saasquatchClient.userUpsert(ImmutableMap.of("accountId", "foo", "id", " "), null));
      assertThrows(NullPointerException.class,
          () -> saasquatchClient.widgetUpsert(
              WidgetUpsertInput.newBuilder().setUserInput(Collections.emptyMap()).build(), null));
      assertThrows(IllegalArgumentException.class, () -> saasquatchClient.widgetUpsert(
          WidgetUpsertInput.newBuilder().setUserInput(Collections.singletonMap("accountId", " "))
              .build(), null));
      assertThrows(IllegalArgumentException.class, () -> saasquatchClient.widgetUpsert(
          WidgetUpsertInput.newBuilder().setUserInput(
              ImmutableMap.of("accountId", "foo", "id", " ")).build(), null));
    }
  }

}
