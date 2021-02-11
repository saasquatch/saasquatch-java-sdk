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
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.Header;

@Internal
public final class Client5SaaSquatchHttpResponse implements SaaSquatchHttpResponse {

  // Lazy init
  private String bodyText;
  // Lazy init
  private Map<String, List<String>> allHeaders;
  private final SimpleHttpResponse response;

  @Internal
  public Client5SaaSquatchHttpResponse(SimpleHttpResponse response) {
    this.response = response;
  }

  @Override
  public String getBodyText() {
    String _bodyText = bodyText;
    if (_bodyText == null) {
      bodyText = _bodyText = InternalUtils.getBodyText(response);
    }
    return _bodyText;
  }

  @Override
  public int getStatusCode() {
    return response.getCode();
  }

  @Override
  public final Map<String, List<String>> getAllHeaders() {
    Map<String, List<String>> _allHeaders = allHeaders;
    if (_allHeaders == null) {
      allHeaders = _allHeaders = _getAllHeaders();
    }
    return _allHeaders;
  }

  private Map<String, List<String>> _getAllHeaders() {
    final Map<String, List<String>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    final Iterator<Header> headerIterator = response.headerIterator();
    while (headerIterator.hasNext()) {
      final Header header = headerIterator.next();
      List<String> values = result.get(header.getName());
      //noinspection Java8MapApi
      if (values == null) {
        values = new ArrayList<>();
        result.put(header.getName(), values);
      }
      values.add(header.getValue());
    }
    final Map<String, List<String>> resultCopy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    for (Map.Entry<String, List<String>> e : result.entrySet()) {
      resultCopy.put(e.getKey(), unmodifiableList(e.getValue()));
    }
    // DO NOT use InternalUtils.unmodifiableMap
    return Collections.unmodifiableMap(resultCopy);
  }

}
