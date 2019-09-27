package com.saasquatch.sdk;

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
public class SaaSquatchClientOptions {

  private final String tenantAlias;
  private final String appDomain;
  private final long requestTimeoutMillis;
  private final long connectTimeoutMillis;

  private SaaSquatchClientOptions(@Nullable String tenantAlias, @Nonnull String appDomain,
      long requestTimeoutMillis, long connectTimeoutMillis) {
    this.tenantAlias = tenantAlias;
    this.appDomain = appDomain;
    this.requestTimeoutMillis = requestTimeoutMillis;
    this.connectTimeoutMillis = connectTimeoutMillis;
  }

  @Nullable
  String getTenantAlias() {
    return tenantAlias;
  }

  @Nonnull
  String getAppDomain() {
    return appDomain;
  }

  long getRequestTimeoutMillis() {
    return requestTimeoutMillis;
  }

  long getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {

    private static final long DEFAULT_REQUEST_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(15);
    private static final long DEFAULT_CONNECT_TIMOEUT_MILLIS = TimeUnit.SECONDS.toMillis(5);

    private String tenantAlias;
    private String appDomain = "app.referralsaasquatch.com";
    private long requestTimeoutMillis = DEFAULT_REQUEST_TIMEOUT_MILLIS;
    private long connectTimeoutMillis = DEFAULT_CONNECT_TIMOEUT_MILLIS;

    private Builder() {}

    /**
     * Set the default tenantAlias that should be used for all requests
     */
    public Builder setTenantAlias(@Nonnull String tenantAlias) {
      this.tenantAlias = Objects.requireNonNull(tenantAlias, "tenantAlias");
      return this;
    }

    /**
     * Set the alternative app domain to use
     *
     * @param appDomain The app domain. Note that it should not have a protocol, and it cannot start
     *        or end with '/'.
     */
    public Builder setAppDomain(@Nonnull String appDomain) {
      this.appDomain = Objects.requireNonNull(appDomain, "appDomain");
      // Validate appDomain
      if (appDomain.contains("://")) {
        throw new IllegalArgumentException("appDomain should not have a protocol");
      }
      if (appDomain.startsWith("/") || appDomain.endsWith("/")) {
        throw new IllegalArgumentException("appDomain should not start or end with a slash");
      }
      return this;
    }

    public Builder setRequestTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      this.requestTimeoutMillis = timeUnit.toMillis(duration);
      if (this.requestTimeoutMillis <= 0) {
        this.requestTimeoutMillis = DEFAULT_REQUEST_TIMEOUT_MILLIS;
        throw new IllegalArgumentException("Invalid timeout");
      }
      return this;
    }

    public Builder setConnectTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      this.connectTimeoutMillis = timeUnit.toMillis(duration);
      if (this.connectTimeoutMillis <= 0) {
        this.connectTimeoutMillis = DEFAULT_CONNECT_TIMOEUT_MILLIS;
        throw new IllegalArgumentException("Invalid timeout");
      }
      return this;
    }

    public SaaSquatchClientOptions build() {
      return new SaaSquatchClientOptions(tenantAlias, appDomain, requestTimeoutMillis,
          connectTimeoutMillis);
    }

  }

}
