package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.auth.AuthMethods;

public class RequestOptionsTest {

  @Test
  public void testNullOrInvalid() {
    final RequestOptions.Builder builder = RequestOptions.newBuilder();
    assertThrows(NullPointerException.class, () -> builder.addHeader(null, null));
    assertThrows(NullPointerException.class, () -> builder.addHeader("a", null));
    assertThrows(NullPointerException.class, () -> builder.addHeader(null, "a"));
    assertThrows(IllegalArgumentException.class, () -> builder.addHeader("\t", "a"));
    assertThrows(IllegalArgumentException.class, () -> builder.addHeader("a", "\t"));
    assertThrows(IllegalArgumentException.class, () -> builder.addHeader("Cookie", "foo"));
    assertThrows(NullPointerException.class, () -> builder.addHeaders("a", "a", "a", null));
    assertThrows(IllegalArgumentException.class, () -> builder.addHeaders("a", "a", "a"));
    assertThrows(NullPointerException.class, () -> builder.addQueryParam(null, null));
    assertThrows(NullPointerException.class, () -> builder.addQueryParam("a", null));
    assertThrows(NullPointerException.class, () -> builder.addQueryParam(null, "a"));
    assertThrows(IllegalArgumentException.class, () -> builder.addQueryParam("\t", "a"));
    assertThrows(IllegalArgumentException.class, () -> builder.addQueryParam("a", "\t"));
    assertThrows(NullPointerException.class, () -> builder.addQueryParams("a", "a", "a", null));
    assertThrows(IllegalArgumentException.class, () -> builder.addQueryParams("a", "a", "a"));
    assertThrows(NullPointerException.class, () -> builder.setTenantAlias(null));
    assertThrows(IllegalArgumentException.class, () -> builder.setTenantAlias("\r"));
  }

  @Test
  public void testBasic() {
    final RequestOptions requestOptions =
        RequestOptions.newBuilder().setTenantAlias("aaaaaaaaaaaaaaaa")
            .setAuthMethod(AuthMethods.ofTenantApiKey("dfsajkglhrejlghdfslghsd"))
            .setRequestTimeout(5, TimeUnit.SECONDS).setConnectTimeout(500, TimeUnit.MILLISECONDS)
            .addHeader("foo", "bar").addHeaders("a", "b", "c", "d").addQueryParam("foo", "bar")
            .addQueryParams("a", "b", "c", "d").setContentCompressionEnabled(true).build();
    assertNotNull(requestOptions.getAuthMethod());
    assertEquals(500, requestOptions.getConnectTimeoutMillis(1));
    assertEquals(5000, requestOptions.getRequestTimeoutMillis(1));
    assertEquals("aaaaaaaaaaaaaaaa", requestOptions.getTenantAlias());
  }

}
