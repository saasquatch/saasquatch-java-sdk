package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.InternalUtils.entryOf;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.net.URIBuilder;
import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.auth.AuthMethod;

/**
 * Request options override, e.g. HTTP headers and query parameters.
 *
 * @author sli
 * @see #newBuilder()
 */
public final class RequestOptions {

  private static final Set<String> BLOCKED_HEADERS;
  static {
    final Set<String> blockedHeaders = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    Collections.addAll(blockedHeaders, HttpHeaders.ACCEPT_ENCODING, HttpHeaders.CONTENT_ENCODING,
        HttpHeaders.CONTENT_LENGTH, HttpHeaders.CONTENT_TYPE, HttpHeaders.ACCEPT,
        HttpHeaders.ACCEPT_CHARSET, "Cookie", "Cookie2", "Set-Cookie", "Set-Cookie2",
        HttpHeaders.HOST, HttpHeaders.CACHE_CONTROL, HttpHeaders.USER_AGENT);
    BLOCKED_HEADERS = Collections.unmodifiableSet(blockedHeaders);
  }

  private final String tenantAlias;
  private final AuthMethod authMethod;
  private final Integer requestTimeoutMillis;
  private final Integer connectTimeoutMillis;
  private final Boolean contentCompressionEnabled;
  private final List<Map.Entry<String, String>> headers;
  private final List<Map.Entry<String, String>> queryParams;

  private RequestOptions(@Nullable String tenantAlias, @Nullable AuthMethod authMethod,
      @Nullable Integer requestTimeoutMillis, @Nullable Integer connectTimeoutMillis,
      @Nullable Boolean contentCompressionEnabled, @Nonnull List<Map.Entry<String, String>> headers,
      @Nonnull List<Map.Entry<String, String>> queryParams) {
    this.tenantAlias = tenantAlias;
    this.authMethod = authMethod;
    this.requestTimeoutMillis = requestTimeoutMillis;
    this.connectTimeoutMillis = connectTimeoutMillis;
    this.contentCompressionEnabled = contentCompressionEnabled;
    this.headers = headers;
    this.queryParams = queryParams;
  }

  @Nullable
  String getTenantAlias() {
    return tenantAlias;
  }

  @Nullable
  AuthMethod getAuthMethod() {
    return authMethod;
  }

  int getRequestTimeoutMillis(int defaultTimeout) {
    return requestTimeoutMillis == null ? defaultTimeout : requestTimeoutMillis;
  }

  int getConnectTimeoutMillis(int defaultTimeout) {
    return connectTimeoutMillis == null ? defaultTimeout : connectTimeoutMillis;
  }

  boolean isContentCompressionEnabled(boolean defaultEnabled) {
    return contentCompressionEnabled == null ? defaultEnabled : contentCompressionEnabled;
  }

  void mutateUrl(@Nonnull URIBuilder urlBuilder) {
    for (final Map.Entry<String, String> e : queryParams) {
      urlBuilder.addParameter(e.getKey(), e.getValue());
    }
  }

  void mutateRequest(@Nonnull SimpleHttpRequest request) {
    for (final Map.Entry<String, String> e : headers) {
      request.addHeader(e.getKey(), e.getValue());
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String tenantAlias;
    private AuthMethod authMethod;
    private Integer requestTimeoutMillis;
    private Integer connectTimeoutMillis;
    private Boolean contentCompressionEnabled;
    private List<Map.Entry<String, String>> headers;
    private List<Map.Entry<String, String>> queryParams;

    private Builder() {}

    /**
     * Override the default tenantAlias for a request
     */
    public Builder setTenantAlias(@Nonnull String tenantAlias) {
      this.tenantAlias = requireNotBlank(tenantAlias, "tenantAlias");
      return this;
    }

    /**
     * Set the {@link AuthMethod} for a request
     */
    public Builder setAuthMethod(@Nonnull AuthMethod authMethod) {
      this.authMethod = Objects.requireNonNull(authMethod);
      return this;
    }

    public Builder setRequestTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      this.requestTimeoutMillis = ClientOptions.validateRequestTimeout(duration, timeUnit);
      return this;
    }

    public Builder setConnectTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      this.connectTimeoutMillis = ClientOptions.validateConnectTimeout(duration, timeUnit);
      return this;
    }

    @Beta
    public Builder setContentCompressionEnabled(boolean contentCompressionEnabled) {
      this.contentCompressionEnabled = contentCompressionEnabled;
      return this;
    }

    /**
     * Add an HTTP header
     */
    public Builder addHeader(@Nonnull String key, @Nonnull String value) {
      requireNotBlank(key, "key");
      requireNotBlank(value, "value");
      if (BLOCKED_HEADERS.contains(key)) {
        throw new IllegalArgumentException(key + " is not allowed");
      }
      if (headers == null) {
        headers = new ArrayList<>();
      }
      headers.add(entryOf(key, value));
      return this;
    }

    /**
     * Convenience method for {@link #addHeader(String, String)} where you can pass in multiple
     * headers
     */
    @Beta
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
     * Add a URL query parameter. Note that the key and value should <em>NOT</em> be URL encoded.
     */
    public Builder addQueryParam(@Nonnull String key, @Nonnull String value) {
      requireNotBlank(key, "key");
      requireNotBlank(value, "value");
      if (queryParams == null) {
        queryParams = new ArrayList<>();
      }
      queryParams.add(entryOf(key, value));
      return this;
    }

    /**
     * Convenience method for {@link #addQueryParam(String, String) where you can pass in multiple
     * query parameters
     */
    @Beta
    public Builder addQueryParams(@Nonnull String... keysAndValues) {
      if ((keysAndValues.length & 1) != 0) {
        throw new IllegalArgumentException("odd number of keys and values");
      }
      for (int i = 0; i < keysAndValues.length;) {
        addQueryParam(keysAndValues[i++], keysAndValues[i++]);
      }
      return this;
    }

    /**
     * Build an immutable {@link RequestOptions}
     */
    public RequestOptions build() {
      return new RequestOptions(tenantAlias, authMethod, requestTimeoutMillis, connectTimeoutMillis,
          contentCompressionEnabled,
          headers == null ? Collections.emptyList() : unmodifiableList(headers),
          queryParams == null ? Collections.emptyList() : unmodifiableList(queryParams));
    }

  }

}
