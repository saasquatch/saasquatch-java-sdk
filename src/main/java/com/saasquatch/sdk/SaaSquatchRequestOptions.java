package com.saasquatch.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Request options override, e.g. HTTP headers and query parameters.
 *
 * @author sli
 */
@Immutable
public final class SaaSquatchRequestOptions {

  private static final Set<String> BLOCKED_HEADERS;
  static {
    final Set<String> blockedHeaders = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    blockedHeaders.addAll(Arrays.asList("Authorization", "Accept-Encoding", "Content-Encoding",
        "Content-Length", "Content-Type", "Accept", "Accept-Charset", "Cookie", "Set-Cookie",
        "Set-Cookie2", "Cache-Control", "Host", "X-SaaSquatch-User-Token"));
    BLOCKED_HEADERS = Collections.unmodifiableSet(blockedHeaders);
  }

  private final String tenantAlias;
  private final Map<String, String> singleHeaders;
  private final List<Entry<String, String>> multiHeaders;
  private final List<Entry<String, String>> queryParams;

  private SaaSquatchRequestOptions(String tenantAlias, Map<String, String> singleHeaders,
      List<Map.Entry<String, String>> multiHeaders, List<Map.Entry<String, String>> queryParams) {
    this.tenantAlias = tenantAlias;
    this.singleHeaders = singleHeaders;
    this.multiHeaders = multiHeaders;
    this.queryParams = queryParams;
  }

  // Not public
  String getTenantAlias() {
    return tenantAlias;
  }

  void mutateRequest(Request.Builder requestBuilder, HttpUrl.Builder urlBuilder) {
    multiHeaders.forEach(e -> requestBuilder.addHeader(e.getKey(), e.getValue()));
    singleHeaders.forEach(requestBuilder::header);
    queryParams.forEach(e -> urlBuilder.addQueryParameter(e.getKey(), e.getValue()));
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String tenantAlias;
    private final Map<String, String> singleHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final List<Map.Entry<String, String>> multiHeaders = new ArrayList<>();
    private final List<Map.Entry<String, String>> queryParams = new ArrayList<>();

    private Builder() {}

    /**
     * Override the default tenantAlias for a request
     */
    public Builder setTenantAlias(@Nonnull String tenantAlias) {
      this.tenantAlias = Objects.requireNonNull(tenantAlias);
      return this;
    }

    /**
     * Set your tenant API key and use it to authenticate your request
     */
    public Builder setApiKey(@Nonnull String apiKey) {
      Objects.requireNonNull(apiKey);
      singleHeaders.put("Authorization", Credentials.basic("", apiKey, UTF_8));
      return this;
    }

    /**
     * Set your JWT and use it to authenticate your request
     */
    public Builder setJwt(@Nonnull String jwt) {
      Objects.requireNonNull(jwt);
      singleHeaders.put("Authorization", "Bearer " + jwt);
      return this;
    }

    /**
     * Add an HTTP header
     */
    public Builder addHeader(@Nonnull String key, @Nonnull String value) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(value);
      if (BLOCKED_HEADERS.contains(key)) {
        throw new IllegalArgumentException(key + " is not allowed");
      }
      multiHeaders.add(new SimpleImmutableEntry<>(key, value));
      return this;
    }

    /**
     * Convenience method for {@link #addHeader(String, String)} where you can pass in multiple
     * headers
     */
    public Builder addHeaders(@Nonnull String... keysAndValues) {
      if ((keysAndValues.length & 1) != 0) {
        throw new IllegalArgumentException("odd number of keys and values");
      }
      for (int i = 0; i < keysAndValues.length;) {
        addHeader(keysAndValues[i++], keysAndValues[i++]);
      }
      return this;
    }

    /**
     * Add a URL query parameter
     */
    public Builder addQueryParam(@Nonnull String key, @Nonnull String value) {
      Objects.requireNonNull(key);
      Objects.requireNonNull(value);
      queryParams.add(new SimpleImmutableEntry<>(key, value));
      return this;
    }

    /**
     * Convenience method for {@link #addQueryParam(String, String)(String, String)} where you can
     * pass in multiple query parameters
     */
    public Builder addQueryParams(@Nonnull String... keysAndValues) {
      if ((keysAndValues.length & 1) != 0) {
        throw new IllegalArgumentException("odd number of keys and values");
      }
      for (int i = 0; i < keysAndValues.length;) {
        addQueryParam(keysAndValues[i++], keysAndValues[i++]);
      }
      return this;
    }

    public SaaSquatchRequestOptions build() {
      return new SaaSquatchRequestOptions(tenantAlias,
          Collections.unmodifiableMap(new HashMap<>(singleHeaders)),
          Collections.unmodifiableList(new ArrayList<>(multiHeaders)),
          Collections.unmodifiableList(new ArrayList<>(queryParams)));
    }

  }

}
