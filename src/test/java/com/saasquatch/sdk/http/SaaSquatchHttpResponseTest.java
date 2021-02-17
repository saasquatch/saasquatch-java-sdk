package com.saasquatch.sdk.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SaaSquatchHttpResponseTest {

  @Test
  public void testBasic() {
    final SimpleHttpResponse r1 = SimpleHttpResponse.create(200, "foo");
    r1.addHeader("abc", "1");
    r1.addHeader("Abc", "2");
    r1.addHeader("aBc", "3");
    final SaaSquatchHttpResponse response = new Client5SaaSquatchHttpResponse(r1);
    assertEquals("1", response.getFirstHeader("abC"));
    assertEquals("3", response.getLastHeader("aBC"));
    assertEquals(Arrays.asList("1", "2", "3"), response.getHeaders("AbC"));
    assertEquals(ImmutableMap.of("abc", Arrays.asList("1", "2", "3")), response.getAllHeaders());
  }

}
