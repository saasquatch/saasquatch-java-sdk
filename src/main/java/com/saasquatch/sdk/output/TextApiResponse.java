package com.saasquatch.sdk.output;

import static com.saasquatch.sdk.internal.InternalUtils.getBodyText;
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
    return getBodyText(response);
  }

}
