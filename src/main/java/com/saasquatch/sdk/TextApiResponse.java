package com.saasquatch.sdk;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

/**
 * {@link ApiResponse} that returns plain text
 *
 * @author sli
 */
public class TextApiResponse extends ApiResponse<String> {

  TextApiResponse(SimpleHttpResponse response) {
    super(response);
  }

  @Override
  protected String buildData() {
    return response.getBodyText();
  }

}
