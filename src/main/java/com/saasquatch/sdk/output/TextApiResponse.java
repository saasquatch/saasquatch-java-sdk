package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Internal;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

/**
 * {@link ApiResponse} that returns plain text
 *
 * @author sli
 */
public final class TextApiResponse extends ApiResponse<String> {

  @Internal
  public TextApiResponse(@Nonnull SimpleHttpResponse response) {
    super(response);
  }

  @Internal
  public TextApiResponse(@Nonnull SimpleHttpResponse response, @Nullable String dataOverride) {
    super(response, dataOverride);
  }

  @Override
  protected String buildData() {
    return getBodyText();
  }

}
