package com.saasquatch.sdk.response;

import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import com.saasquatch.sdk.annotations.Internal;

/**
 * {@link ApiResponse} that returns plain text
 *
 * @author sli
 */
public final class TextApiResponse extends ApiResponse<String> {

  @Internal
  public TextApiResponse(SimpleHttpResponse response) {
    super(response);
  }

  @Override
  protected String buildData() {
    return response.getBodyText();
  }

}
