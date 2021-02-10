package com.saasquatch.sdk.util;

import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableList;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.InternalUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.Header;

@Internal
public final class Client5SaaSquatchHttpResponse implements SaaSquatchHttpResponse {

  // Lazy init
  private String bodyText;
  private final SimpleHttpResponse response;

  @Internal
  public Client5SaaSquatchHttpResponse(SimpleHttpResponse response) {
    this.response = response;
  }

  @Override
  public String getBodyText() {
    String s = bodyText;
    if (s == null) {
      bodyText = s = InternalUtils.getBodyText(response);
    }
    return s;
  }

  @Override
  public int getStatusCode() {
    return response.getCode();
  }

  @Nonnull
  @Override
  public final List<String> getHeaders(String headerName) {
    final List<String> result = new ArrayList<>();
    final Iterator<Header> headerIterator = response.headerIterator(headerName);
    while (headerIterator.hasNext()) {
      result.add(headerIterator.next().getValue());
    }
    return unmodifiableList(result);
  }

  @Nullable
  @Override
  public final String getFirstHeader(String headerName) {
    final Header header = response.getFirstHeader(headerName);
    return header == null ? null : header.getValue();
  }

  @Nullable
  @Override
  public final String getLastHeader(String headerName) {
    final Header header = response.getLastHeader(headerName);
    return header == null ? null : header.getValue();
  }

  @Override
  public final Map<String, List<String>> getAllHeaders() {
    final Map<String, List<String>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    final Iterator<Header> headerIterator = response.headerIterator();
    while (headerIterator.hasNext()) {
      final Header header = headerIterator.next();
      List<String> values = result.get(header.getName());
      if (values == null) {
        values = new ArrayList<>();
        result.put(header.getName(), values);
      }
      values.add(header.getValue());
    }
    return Collections.unmodifiableMap(result);
  }

}
