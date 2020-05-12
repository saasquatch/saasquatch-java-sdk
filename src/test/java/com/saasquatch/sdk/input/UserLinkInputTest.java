package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class UserLinkInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> UserLinkInput.newBuilder().build());
    assertThrows(NullPointerException.class,
        () -> UserLinkInput.newBuilder().setAccountId("a").build());
    assertThrows(NullPointerException.class,
        () -> UserLinkInput.newBuilder().setUserId("a").build());
  }

  @Test
  public void testBasic() {
    final UserLinkInput userLinkInput = UserLinkInput.newBuilder().setAccountId("a").setUserId("u")
        .setProgramId("p").setEngagementMedium("EMAIL").setShareMedium("FACEBOOK").build();
    assertEquals("a", userLinkInput.getAccountId());
    assertEquals("u", userLinkInput.getUserId());
    assertEquals("p", userLinkInput.getProgramId());
    assertEquals("EMAIL", userLinkInput.getEngagementMedium());
    assertEquals("FACEBOOK", userLinkInput.getShareMedium());
  }

}
