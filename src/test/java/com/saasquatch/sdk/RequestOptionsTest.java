package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.core5.net.URIBuilder;
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
    assertTrue(requestOptions.isContentCompressionEnabled(false));
    assertEquals(123, RequestOptions.newBuilder().build().getConnectTimeoutMillis(123));
    assertEquals(123, RequestOptions.newBuilder().build().getRequestTimeoutMillis(123));
    assertTrue(RequestOptions.newBuilder().build().isContentCompressionEnabled(true));
    assertFalse(RequestOptions.newBuilder().build().isContentCompressionEnabled(false));
  }

  @Test
  public void testRequestMutation() throws Exception {
    final RequestOptions requestOptions =
        RequestOptions.newBuilder().addHeader("a", "b").addQueryParam("c", "d").build();
    final SimpleHttpRequest request = SimpleHttpRequests.get("http://app.referralsaasquatch.com");
    assertNull(request.getFirstHeader("a"));
    requestOptions.mutateRequest(request);
    assertEquals("b", request.getFirstHeader("a").getValue());
    final URIBuilder uriBuilder = new URIBuilder("http://example.com");
    assertTrue(uriBuilder.getQueryParams().isEmpty());
    requestOptions.mutateUrl(uriBuilder);
    assertEquals(1, uriBuilder.getQueryParams().size());
    assertEquals("c", uriBuilder.getQueryParams().get(0).getName());
    assertEquals("d", uriBuilder.getQueryParams().get(0).getValue());
  }

}
