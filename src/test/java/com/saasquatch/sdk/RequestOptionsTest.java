package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class RequestOptionsTest {

  @Test
  public void testNullOrInvalid() {
    final RequestOptions.Builder builder = RequestOptions.newBuilder();
    assertThrows(NullPointerException.class, () -> builder.addHeader(null, null));
    assertThrows(NullPointerException.class, () -> builder.addHeader("a", null));
    assertThrows(NullPointerException.class, () -> builder.addHeader(null, "a"));
    assertThrows(IllegalArgumentException.class, () -> builder.addHeader("\t", "a"));
    assertThrows(IllegalArgumentException.class, () -> builder.addHeader("a", "\t"));
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

}
