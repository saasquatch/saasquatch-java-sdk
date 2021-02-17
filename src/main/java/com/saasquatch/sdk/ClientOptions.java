package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.InternalUtils.format;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.auth.AuthMethod;

/**
 * Options for a {@link SaaSquatchClient}
 *
 * @author sli
 * @see #newBuilder()
 */
public final class ClientOptions {

  private static final String DEFAULT_APP_DOMAIN = "app.referralsaasquatch.com";
  private static final int DEFAULT_MAX_CONCURRENT_REQUESTS = 2;
  private static final int MAX_MAX_CONCURRENT_REQUESTS = 32;
  static final int DEFAULT_REQUEST_TIMEOUT_MILLIS = 10000;
  static final int MAX_REQUEST_TIMEOUT_MILLIS = 60000;
  static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 5000;
  static final int MAX_CONNECT_TIMEOUT_MILLIS = 30000;

  private final String tenantAlias;
  private final AuthMethod authMethod;
  private final String appDomain;
  private final int maxConcurrentRequests;
  private final int requestTimeoutMillis;
  private final int connectTimeoutMillis;
  private final boolean contentCompressionEnabled;

  private ClientOptions(@Nullable String tenantAlias, @Nullable AuthMethod authMethod,
      @Nonnull String appDomain, int maxConcurrentRequests, int requestTimeoutMillis,
      int connectTimeoutMillis, boolean contentCompressionEnabled) {
    this.tenantAlias = tenantAlias;
    this.authMethod = authMethod;
    this.appDomain = appDomain;
    this.maxConcurrentRequests = maxConcurrentRequests;
    this.requestTimeoutMillis = requestTimeoutMillis;
    this.connectTimeoutMillis = connectTimeoutMillis;
    this.contentCompressionEnabled = contentCompressionEnabled;
  }

  @Nullable
  String getTenantAlias() {
    return tenantAlias;
  }

  @Nullable
  AuthMethod getAuthMethod() {
    return authMethod;
  }

  @Nonnull
  String getAppDomain() {
    return appDomain;
  }

  int getMaxConcurrentRequests() {
    return maxConcurrentRequests;
  }

  int getRequestTimeoutMillis() {
    return requestTimeoutMillis;
  }

  int getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  boolean isContentCompressionEnabled() {
    return contentCompressionEnabled;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  static int validateRequestTimeout(long duration, @Nonnull TimeUnit timeUnit) {
    final int millis = (int) timeUnit.toMillis(duration);
    if (millis <= 0) {
      throw new IllegalArgumentException("non-positive timeout");
    }
    if (millis > MAX_REQUEST_TIMEOUT_MILLIS) {
      throw new IllegalArgumentException(format("requestTimeout cannot be greater than %d seconds",
          TimeUnit.MILLISECONDS.toSeconds(MAX_REQUEST_TIMEOUT_MILLIS)));
    }
    return millis;
  }

  static int validateConnectTimeout(long duration, @Nonnull TimeUnit timeUnit) {
    final int millis = (int) timeUnit.toMillis(duration);
    if (millis <= 0) {
      throw new IllegalArgumentException("non-positive timeout");
    }
    if (millis > MAX_CONNECT_TIMEOUT_MILLIS) {
      throw new IllegalArgumentException(format("connectTimeout cannot be greater than %d seconds",
          TimeUnit.MILLISECONDS.toSeconds(MAX_CONNECT_TIMEOUT_MILLIS)));
    }
    return millis;
  }

  public static final class Builder {

    private String tenantAlias;
    private AuthMethod authMethod;
    private String appDomain = DEFAULT_APP_DOMAIN;
    private int maxConcurrentRequests = DEFAULT_MAX_CONCURRENT_REQUESTS;
    private int requestTimeoutMillis = DEFAULT_REQUEST_TIMEOUT_MILLIS;
    private int connectTimeoutMillis = DEFAULT_CONNECT_TIMEOUT_MILLIS;
    private boolean contentCompressionEnabled = true;

    private Builder() {}

    /**
     * Set the default tenantAlias that should be used for all requests
     */
    public Builder setTenantAlias(@Nonnull String tenantAlias) {
      this.tenantAlias = requireNotBlank(tenantAlias, "tenantAlias");
      return this;
    }

    /**
     * Set the default {@link AuthMethod} for all requests. Note that if you set the
     * {@link AuthMethod}, a default tenantAlias is required.
     */
    public Builder setAuthMethod(@Nonnull AuthMethod authMethod) {
      this.authMethod = Objects.requireNonNull(authMethod, "authMethod");
      return this;
    }

    /**
     * Set the alternative app domain to use
     *
     * @param appDomain The app domain. Note that it should not have a protocol, and it cannot start
     *        or end with '/'.
     */
    public Builder setAppDomain(@Nonnull String appDomain) {
      requireNotBlank(appDomain, "appDomain");
      if (appDomain.contains("://")) {
        throw new IllegalArgumentException("appDomain should not have a protocol");
      }
      if (appDomain.startsWith("/") || appDomain.endsWith("/")) {
        throw new IllegalArgumentException("appDomain should not start or end with a slash");
      }
      this.appDomain = appDomain;
      return this;
    }

    public Builder setMaxConcurrentRequests(int maxConcurrentRequests) {
      if (maxConcurrentRequests <= 0) {
        throw new IllegalArgumentException("non-positive maxConcurrentRequests");
      }
      if (maxConcurrentRequests > MAX_MAX_CONCURRENT_REQUESTS) {
        throw new IllegalArgumentException(
            "maxConcurrentRequests cannot be greater than " + MAX_MAX_CONCURRENT_REQUESTS);
      }
      this.maxConcurrentRequests = maxConcurrentRequests;
      return this;
    }

    public Builder setRequestTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      this.requestTimeoutMillis = validateRequestTimeout(duration, timeUnit);
      return this;
    }

    public Builder setConnectTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      this.connectTimeoutMillis = validateConnectTimeout(duration, timeUnit);
      return this;
    }

    @Beta
    public Builder setContentCompressionEnabled(boolean contentCompressionEnabled) {
      this.contentCompressionEnabled = contentCompressionEnabled;
      return this;
    }

    /**
     * Build an immutable {@link ClientOptions}
     */
    public ClientOptions build() {
      if (authMethod != null && tenantAlias == null) {
        throw new IllegalArgumentException("tenantAlias is required if you set the authMethod");
      }
      return new ClientOptions(tenantAlias, authMethod, appDomain, maxConcurrentRequests,
          requestTimeoutMillis, connectTimeoutMillis, contentCompressionEnabled);
    }

  }

}
