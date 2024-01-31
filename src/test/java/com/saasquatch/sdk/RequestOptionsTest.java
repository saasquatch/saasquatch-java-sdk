package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.saasquatch.sdk.auth.AuthMethod;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.core5.net.URIBuilder;
import org.junit.jupiter.api.Test;

public class RequestOptionsTest {

  @SuppressWarnings("ConstantConditions")
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

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testBasic() {
    final RequestOptions requestOptions =
        RequestOptions.newBuilder().setTenantAlias("aaaaaaaaaaaaaaaa")
            .setAuthMethod(AuthMethod.ofTenantApiKey("dfsajkglhrejlghdfslghsd"))
            .setRequestTimeout(5, TimeUnit.SECONDS)
            .addHeader("foo", "bar").addHeaders("a", "b", "c", "d").addQueryParam("foo", "bar")
            .addQueryParams("a", "b", "c", "d").setContentCompressionEnabled(true).build();
    assertNotNull(requestOptions.getAuthMethod());
    assertEquals(5000, requestOptions.getRequestTimeoutMillis());
    assertEquals("aaaaaaaaaaaaaaaa", requestOptions.getTenantAlias());
    assertNotNull(requestOptions.getContentCompressionEnabled());
    assertTrue(requestOptions.getContentCompressionEnabled());
    assertNull(RequestOptions.newBuilder().build().getContentCompressionEnabled());
  }

  @Test
  public void testRequestMutation() throws Exception {
    final RequestOptions requestOptions =
        RequestOptions.newBuilder().addHeader("a", "b").addQueryParam("c", "d").build();
    final SimpleRequestBuilder requestBuilder =
        SimpleRequestBuilder.get("http://app.referralsaasquatch.com");
    assertNull(requestBuilder.getFirstHeader("a"));
    requestOptions.mutateRequest(requestBuilder);
    assertEquals("b", requestBuilder.getFirstHeader("a").getValue());
    final URIBuilder uriBuilder = new URIBuilder("http://example.com");
    assertTrue(uriBuilder.getQueryParams().isEmpty());
    requestOptions.mutateUri(uriBuilder);
    assertEquals(1, uriBuilder.getQueryParams().size());
    assertEquals("c", uriBuilder.getQueryParams().get(0).getName());
    assertEquals("d", uriBuilder.getQueryParams().get(0).getValue());
  }

}
