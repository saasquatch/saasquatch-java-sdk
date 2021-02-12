package com.saasquatch.sdk.output;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.http.SaaSquatchHttpResponse;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link ApiResponse} that returns plain text
 *
 * @author sli
 */
public final class TextApiResponse extends ApiResponse<String> {

  @Internal
  public TextApiResponse(@Nonnull SaaSquatchHttpResponse httpResponse) {
    super(httpResponse);
  }

  @Internal
  public TextApiResponse(@Nonnull SaaSquatchHttpResponse httpResponse, @Nullable String dataOverride) {
    super(httpResponse, dataOverride);
  }

  @Override
  protected String buildData() {
    return getHttpResponse().getBodyText();
  }

}
