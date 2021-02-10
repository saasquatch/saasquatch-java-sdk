package com.saasquatch.sdk.util;

import com.saasquatch.sdk.annotations.NoExternalImpl;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Dependency agnostic basic HTTP response info
 *
 * @author sli
 */
@NoExternalImpl
public interface SaaSquatchHttpResponse {

  /**
   * @return The response body text
   */
  String getBodyText();

  /**
   * @return The HTTP status code
   */
  int getStatusCode();

  /**
   * @return All the header values for the given header name
   */
  @Nonnull
  List<String> getHeaders(String headerName);

  /**
   * @return The first header value for the given header name
   */
  @Nullable
  default String getFirstHeader(String headerName) {
    final List<String> headers = getHeaders(headerName);
    return headers.isEmpty() ? null : headers.get(0);
  }

  /**
   * @return The last header value for the given header name
   */
  @Nullable
  default String getLastHeader(String headerName) {
    final List<String> headers = getHeaders(headerName);
    return headers.isEmpty() ? null : headers.get(headers.size() - 1);
  }

  /**
   * @return All the headers
   */
  Map<String, List<String>> getAllHeaders();

}
