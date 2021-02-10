package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.util.SaaSquatchHttpResponse;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link ApiResponse} that returns plain text
 *
 * @author sli
 */
public final class TextApiResponse extends ApiResponse<String> {

  @Internal
  public TextApiResponse(@Nonnull SaaSquatchHttpResponse response) {
    super(response);
  }

  @Internal
  public TextApiResponse(@Nonnull SaaSquatchHttpResponse response, @Nullable String dataOverride) {
    super(response, dataOverride);
  }

  @Override
  protected String buildData() {
    return getHttpResponse().getBodyText();
  }

}
