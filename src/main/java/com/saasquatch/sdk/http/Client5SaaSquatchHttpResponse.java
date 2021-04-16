package com.saasquatch.sdk.http;

import static com.saasquatch.sdk.internal.InternalUtils.collectHeaders;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.InternalUtils;
import java.util.List;
import java.util.Map;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;

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
      allHeaders = _allHeaders = collectHeaders(response);
    }
    return _allHeaders;
  }

}
