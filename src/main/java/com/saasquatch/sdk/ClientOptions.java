package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.requireNotBlank;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/**
 * Options for a {@link SaaSquatchClient}
 *
 * @author sli
 * @see #newBuilder()
 */
@Immutable
public class ClientOptions {

  private static final String DEFAULT_APP_DOMAIN = "app.referralsaasquatch.com";
  private static final int DEFAULT_REQUEST_TIMEOUT_MILLIS = 10000;
  private static final int DEFAULT_CONNECT_TIMOEUT_MILLIS = 2500;
  private static final int MAX_REQUEST_TIMEOUT_MILLIS = 30000;
  private static final int MAX_CONNECT_TIMEOUT_MILLIS = 15000;
  private static final int DEFAULT_MAX_REQUESTS = 2;
  private static final int MAX_MAX_REQUESTS = 20;

  private final String tenantAlias;
  private final AuthMethod authMethod;
  private final String appDomain;
  private final int requestTimeoutMillis;
  private final int connectTimeoutMillis;
  private final int maxConcurrentRequests;

  private ClientOptions(@Nullable String tenantAlias, @Nullable AuthMethod authMethod,
      @Nonnull String appDomain, int requestTimeoutMillis, int connectTimeoutMillis,
      int maxConcurrentRequests) {
    this.tenantAlias = tenantAlias;
    this.authMethod = authMethod;
    this.appDomain = appDomain;
    this.requestTimeoutMillis = requestTimeoutMillis;
    this.connectTimeoutMillis = connectTimeoutMillis;
    this.maxConcurrentRequests = maxConcurrentRequests;
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

  int getRequestTimeoutMillis() {
    return requestTimeoutMillis;
  }

  int getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  int getMaxConcurrentRequests() {
    return maxConcurrentRequests;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private String tenantAlias;
    private AuthMethod authMethod;
    private String appDomain = DEFAULT_APP_DOMAIN;
    private int requestTimeoutMillis = DEFAULT_REQUEST_TIMEOUT_MILLIS;
    private int connectTimeoutMillis = DEFAULT_CONNECT_TIMOEUT_MILLIS;
    private int maxConcurrentRequests = DEFAULT_MAX_REQUESTS;

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
      Objects.requireNonNull(authMethod, "authMethod");
      if (!authMethod.canBeClientDefault()) {
        throw new IllegalArgumentException(
            "The authMethod you have specified cannot be used as the client default");
      }
      this.authMethod = authMethod;
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
      if (!appDomain.matches("[a-zA-Z0-9\\.\\/]+")) {
        throw new IllegalArgumentException("appDomain contains invalid characters");
      }
      this.appDomain = appDomain;
      return this;
    }

    public Builder setRequestTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      final int millis = (int) timeUnit.toMillis(duration);
      if (millis <= 0) {
        throw new IllegalArgumentException("non-positive timeout");
      }
      if (millis > MAX_REQUEST_TIMEOUT_MILLIS) {
        throw new IllegalArgumentException("timeout too large");
      }
      this.requestTimeoutMillis = millis;
      return this;
    }

    public Builder setConnectTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      final int millis = (int) timeUnit.toMillis(duration);
      if (millis <= 0) {
        throw new IllegalArgumentException("non-positive timeout");
      }
      if (millis > MAX_CONNECT_TIMEOUT_MILLIS) {
        throw new IllegalArgumentException("timeout too large");
      }
      this.connectTimeoutMillis = millis;
      return this;
    }

    public Builder setMaxConcurrentRequests(int maxConcurrentRequests) {
      if (maxConcurrentRequests <= 0) {
        throw new IllegalArgumentException("non-positive maxConcurrentRequests");
      }
      if (maxConcurrentRequests > MAX_MAX_REQUESTS) {
        throw new IllegalArgumentException("maxConcurrentRequests too large");
      }
      this.maxConcurrentRequests = maxConcurrentRequests;
      return this;
    }

    /**
     * Build an immutable {@link ClientOptions}
     */
    public ClientOptions build() {
      if (authMethod != null && tenantAlias == null) {
        throw new IllegalArgumentException("tenantAlias is required if you set the authMethod");
      }
      return new ClientOptions(tenantAlias, authMethod, appDomain, requestTimeoutMillis,
          connectTimeoutMillis, maxConcurrentRequests);
    }

  }

}
