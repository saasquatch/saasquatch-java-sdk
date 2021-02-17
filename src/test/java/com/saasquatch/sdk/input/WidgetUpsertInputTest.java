package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

public class WidgetUpsertInputTest {

  @Test
  public void testUserIdAndAccountId() {
    {
      final WidgetUpsertInput widgetUpsertInput = WidgetUpsertInput.newBuilder()
          .setUserInput(UserInput.newBuilder().setAccountId("a").setId("u").build()).build();
      assertEquals("a", widgetUpsertInput.getAccountId());
      assertEquals("u", widgetUpsertInput.getUserId());
    }
    {
      final WidgetUpsertInput widgetUpsertInput = WidgetUpsertInput.newBuilder()
          .setUserInput(ImmutableMap.of("accountId", "a", "id", "u")).build();
      assertEquals("a", widgetUpsertInput.getAccountId());
      assertEquals("u", widgetUpsertInput.getUserId());
    }
  }

}
