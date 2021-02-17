package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.json.GsonUtils.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class UserInputTest {

  @Test
  public void testCustomJson() {
    final UserInput userInput = UserInput.newBuilder().setAccountId("a").setId("a")
        .setCustomFields(Collections.singletonMap("foo", null)).build();
    final String jsonStr = gson.toJson(userInput);
    assertEquals("{\"accountId\":\"a\",\"id\":\"a\",\"customFields\":{\"foo\":null}}", jsonStr);
  }

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> UserInput.newBuilder().build());
    assertThrows(NullPointerException.class,
        () -> UserInput.newBuilder().setAccountId("a").build());
    assertThrows(NullPointerException.class, () -> UserInput.newBuilder().setId("a").build());
  }

  @Test
  public void testBasic() {
    final UserInput userInput = UserInput.newBuilder().setAccountId("a").setId("u")
        .setEmail("foo@example.com").setFirstName("firstName").setLastName("lastName")
        .setLocale("en_US").setImageUrl("http://example.com").setPaymentProviderId("ppid")
        .setReferable(true).setReferralCode("code1")
        .setReferralCodes(Collections.singletonMap("foo", "code2"))
        .setReferredBy(Collections.emptyMap()).setReferredByCodes(Collections.singleton("code3"))
        .setCustomFields(Collections.singletonMap("foo", "bar"))
        .addToSegments("seg1", "seg2").removeFromSegments("seg3").build();
    assertEquals("a", userInput.getAccountId());
    assertEquals("u", userInput.getId());
    assertEquals("foo@example.com", userInput.getEmail());
    assertEquals("firstName", userInput.getFirstName());
    assertEquals("lastName", userInput.getLastName());
    assertEquals("en_US", userInput.getLocale());
    assertEquals("http://example.com", userInput.getImageUrl());
    assertEquals("ppid", userInput.getPaymentProviderId());
    assertTrue(userInput.getReferable());
    assertEquals("code1", userInput.getReferralCode());
    assertEquals(Collections.singletonMap("foo", "code2"), userInput.getReferralCodes());
    assertEquals(Collections.emptyMap(), userInput.getReferredBy());
    assertEquals(Collections.singleton("code3"), userInput.getReferredByCodes());
    assertEquals(Collections.singletonMap("foo", "bar"), userInput.getCustomFields());
    assertEquals(ImmutableSet.of("seg1", "seg2", "~seg3"), userInput.getSegments());
  }

  @Test
  public void testSegments() {
    assertEquals(ImmutableSet.of("seg1", "seg2"), UserInput.newBuilder().setAccountId("a")
        .setId("a").addToSegments("seg1", "seg2").build().getSegments());
    assertEquals(ImmutableSet.of("~seg1", "~seg2"), UserInput.newBuilder().setAccountId("a")
        .setId("a").removeFromSegments("seg1", "seg2").build().getSegments());
  }

}
