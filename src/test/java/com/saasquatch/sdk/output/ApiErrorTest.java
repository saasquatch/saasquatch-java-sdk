package com.saasquatch.sdk.output;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.junit.jupiter.api.Test;
import com.google.gson.JsonObject;

public class ApiErrorTest {

  @Test
  public void testBasic() {
    final JsonObject j = new JsonObject();
    j.addProperty("message", "foo");
    j.addProperty("apiErrorCode", "bar");
    j.addProperty("statusCode", 400);
    j.addProperty("rsCode", "RS001");
    final SimpleHttpResponse response = new SimpleHttpResponse(400);
    response.setBody(j.toString(), ContentType.APPLICATION_JSON);
    final ApiError apiError = ApiError.fromResponse(response);
    assertEquals("foo", apiError.getMessage());
    assertEquals("bar", apiError.getApiErrorCode());
    assertEquals(400, apiError.getStatusCode());
    assertEquals("RS001", apiError.getRsCode());
  }

  @Test
  public void testCatastrophicFailure() {
    final JsonObject j = new JsonObject();
    j.addProperty("statusCode", 401);
    j.addProperty("iDontKnowWhatThisFieldIs", true);
    final SimpleHttpResponse response = new SimpleHttpResponse(400);
    response.setBody(j.toString(), ContentType.APPLICATION_JSON);
    final ApiError apiError = ApiError.fromResponse(response);
    assertEquals(j.toString(), apiError.getMessage());
    assertEquals(400, apiError.getStatusCode());
  }

  @Test
  public void testCatastrophicFailureWithHtml() {
    final String htmlStr = "<p>lol</p>";
    final SimpleHttpResponse response = new SimpleHttpResponse(400);
    response.setBody(htmlStr, ContentType.TEXT_HTML);
    final ApiError apiError = ApiError.fromResponse(response);
    assertEquals(htmlStr, apiError.getMessage());
    assertEquals(400, apiError.getStatusCode());
  }

}
