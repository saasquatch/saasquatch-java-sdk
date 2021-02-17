package com.saasquatch.sdk.http;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableMap;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
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
    assertEquals(200, response.getStatusCode());
    assertEquals("foo", response.getBodyText());
  }

  @Test
  public void testGzip() throws Exception {
    final String bodyText = "456489489624862476894786478652786527865427864127864176878621786";
    final byte[] gzipBytes;
    try (ByteArrayOutputStream baOut = new ByteArrayOutputStream()) {
      try (GZIPOutputStream gzipOut = new GZIPOutputStream(baOut)) {
        gzipOut.write(bodyText.getBytes(UTF_8));
      }
      gzipBytes = baOut.toByteArray();
    }
    final SimpleHttpResponse r1 = SimpleHttpResponse.create(400, gzipBytes);
    r1.addHeader("content-encoding", "gzip");
    final SaaSquatchHttpResponse response = new Client5SaaSquatchHttpResponse(r1);
    assertEquals(bodyText, response.getBodyText());
  }

}
