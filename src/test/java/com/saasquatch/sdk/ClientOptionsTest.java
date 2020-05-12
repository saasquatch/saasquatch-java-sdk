package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.auth.AuthMethods;

public class ClientOptionsTest {

  @Test
  public void testNullAndInvalid() {
    final ClientOptions.Builder builder = ClientOptions.newBuilder();
    assertThrows(NullPointerException.class, () -> builder.setTenantAlias(null));
    assertThrows(IllegalArgumentException.class, () -> builder.setTenantAlias("\r\n"));
    assertThrows(NullPointerException.class, () -> builder.setAppDomain(null));
    assertThrows(IllegalArgumentException.class, () -> builder.setAppDomain("\n"));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setAppDomain("https://app.referralsaasquatch.com"));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setAppDomain("app.referralsaasquatch.com/"));
    assertThrows(IllegalArgumentException.class,
        () -> builder.setAppDomain("/app.referralsaasquatch.com"));
    assertDoesNotThrow(() -> builder.setAppDomain("localhost:80"));
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
    assertThrows(IllegalArgumentException.class, () -> builder.setMaxConcurrentRequests(100));
    assertThrows(IllegalArgumentException.class, () -> builder.setMaxConcurrentRequests(0));
    assertThrows(IllegalArgumentException.class,
        () -> ClientOptions.newBuilder().setAuthMethod(AuthMethods.ofTenantApiKey("foo")).build());
  }

  @Test
  public void testBasic() {
    final ClientOptions clientOptions =
        ClientOptions.newBuilder().setAppDomain("www.example.com").setTenantAlias("aaaaaaaaaaaaa")
            .setAuthMethod(AuthMethods.ofTenantApiKey("dasfjklagrhwejklhfjk"))
            .setConnectTimeout(500, TimeUnit.MILLISECONDS).setRequestTimeout(5, TimeUnit.SECONDS)
            .setMaxConcurrentRequests(10).setContentCompressionEnabled(false).build();
    assertEquals("www.example.com", clientOptions.getAppDomain());
    assertEquals("aaaaaaaaaaaaa", clientOptions.getTenantAlias());
    assertNotNull(clientOptions.getAuthMethod());
    assertEquals(500, clientOptions.getConnectTimeoutMillis());
    assertEquals(5000, clientOptions.getRequestTimeoutMillis());
    assertEquals(10, clientOptions.getMaxConcurrentRequests());
    assertFalse(clientOptions.isContentCompressionEnabled());
  }

}
