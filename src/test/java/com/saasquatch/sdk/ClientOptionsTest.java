package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class ClientOptionsTest {

  @Test
  public void testNullAndInvalid() {
    final ClientOptions.Builder builder = ClientOptions.newBuilder();
    assertThrows(NullPointerException.class, () -> builder.setAppDomain(null));
    assertThrows(IllegalArgumentException.class, () -> builder.setAppDomain("\n"));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setAppDomain("https://app.referralsaasquatch.com"));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setAppDomain("app.referralsaasquatch.com/"));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setAppDomain("/app.referralsaasquatch.com"));
    assertThrows(NullPointerException.class, () -> builder.setRequestTimeout(1, null));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setRequestTimeout(0, TimeUnit.SECONDS));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setRequestTimeout(100, TimeUnit.SECONDS));
    assertThrows(NullPointerException.class, () -> builder.setConnectTimeout(1, null));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setConnectTimeout(0, TimeUnit.SECONDS));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setConnectTimeout(45, TimeUnit.SECONDS));
    assertThrows(NullPointerException.class, () -> builder.setAuthMethod(null));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setAuthMethod(AuthMethod.ofJwt("abc")));
  }

}
