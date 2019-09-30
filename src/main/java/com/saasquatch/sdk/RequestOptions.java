package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.entryOf;
import static com.saasquatch.sdk.InternalUtils.unmodifiableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import okhttp3.HttpUrl;
import okhttp3.Request;

/**
 * Request options override, e.g. HTTP headers and query parameters.
 *
 * @author sli
 * @see #newBuilder()
 */
@Immutable
public final class RequestOptions {

  private static final Set<String> BLOCKED_HEADERS;
  static {
    final Set<String> blockedHeaders = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    blockedHeaders.addAll(Arrays.asList("Authorization", "Accept-Encoding", "Content-Encoding",
        "Content-Length", "Content-Type", "Accept", "Accept-Charset", "Cookie", "Set-Cookie",
        "Set-Cookie2", "Cache-Control", "Host", "X-SaaSquatch-User-Token"));
    BLOCKED_HEADERS = Collections.unmodifiableSet(blockedHeaders);
  }

  private final String tenantAlias;
  private final AuthMethod authMethod;
  private final List<Map.Entry<String, String>> headers;
  private final List<Map.Entry<String, String>> queryParams;

  private RequestOptions(@Nullable String tenantAlias, @Nullable AuthMethod authMethod,
      @Nonnull List<Map.Entry<String, String>> multiHeaders,
      @Nonnull List<Map.Entry<String, String>> queryParams) {
    this.tenantAlias = tenantAlias;
    this.authMethod = authMethod;
    this.headers = multiHeaders;
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

  void mutateRequest(@Nonnull Request.Builder requestBuilder, @Nonnull HttpUrl.Builder urlBuilder) {
    for (final Map.Entry<String, String> e : headers) {
      requestBuilder.addHeader(e.getKey(), e.getValue());
    }
    for (final Map.Entry<String, String> e : queryParams) {
      urlBuilder.addQueryParameter(e.getKey(), e.getValue());
    }
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String tenantAlias;
    private AuthMethod authMethod;
    private final List<Map.Entry<String, String>> headers = new ArrayList<>();
    private final List<Map.Entry<String, String>> queryParams = new ArrayList<>();

    private Builder() {}

    /**
     * Override the default tenantAlias for a request
     */
    public Builder setTenantAlias(@Nonnull String tenantAlias) {
      if (Objects.requireNonNull(tenantAlias, "tenantAlias").trim().isEmpty()) {
        throw new IllegalArgumentException("Blank tenantAlias");
      }
      this.tenantAlias = tenantAlias;
      return this;
    }

    /**
     * Set the {@link AuthMethod} for a request
     */
    public Builder setAuthMethod(@Nonnull AuthMethod authMethod) {
      this.authMethod = Objects.requireNonNull(authMethod);
      return this;
    }

    /**
     * Add an HTTP header
     */
    public Builder addHeader(@Nonnull String key, @Nonnull String value) {
      if (Objects.requireNonNull(key, "key").trim().isEmpty()) {
        throw new IllegalArgumentException("Blank key");
      }
      if (Objects.requireNonNull(value, "value").trim().isEmpty()) {
        throw new IllegalArgumentException("Blank key");
      }
      if (BLOCKED_HEADERS.contains(key)) {
        throw new IllegalArgumentException(key + " is not allowed");
      }
      headers.add(entryOf(key, value));
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
     * Add a URL query parameter. Note that the key and value are expected to be <em>unencoded</em>.
     */
    public Builder addQueryParam(@Nonnull String key, @Nonnull String value) {
      if (Objects.requireNonNull(key, "key").trim().isEmpty()) {
        throw new IllegalArgumentException("Blank key");
      }
      if (Objects.requireNonNull(value, "value").trim().isEmpty()) {
        throw new IllegalArgumentException("Blank key");
      }
      queryParams.add(entryOf(key, value));
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

    /**
     * Build an immutable {@link RequestOptions}
     */
    public RequestOptions build() {
      return new RequestOptions(tenantAlias, authMethod, unmodifiableList(headers),
          unmodifiableList(queryParams));
    }

  }

}
