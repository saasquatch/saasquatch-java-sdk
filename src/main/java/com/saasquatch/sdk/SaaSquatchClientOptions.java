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
 */
@Immutable
public class SaaSquatchClientOptions {

  private final String tenantAlias;
  private final String appDomain;
  private final long requestTimeoutMillis;
  private final long connectTimeoutMillis;

  private SaaSquatchClientOptions(String tenantAlias, String appDomain, long requestTimeoutMillis,
      long connectTimeoutMillis) {
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

    private String tenantAlias;
    private String appDomain = "app.referralsaasquatch.com";
    private long requestTimeoutMillis = TimeUnit.SECONDS.toMillis(15);
    private long connectTimeoutMillis = TimeUnit.SECONDS.toMillis(5);

    private Builder() {}

    /**
     * Set the default tenantAlias that should be used for all requests
     */
    public Builder setTenantAlias(@Nonnull String tenantAlias) {
      this.tenantAlias = Objects.requireNonNull(tenantAlias);
      return this;
    }

    /**
     * Set the alternative app domain to use
     *
     * @param appDomain The app domain. Note that it should not have a protocol, and it cannot start
     *        or end with '/'.
     */
    public Builder setAppDomain(@Nonnull String appDomain) {
      this.appDomain = Objects.requireNonNull(appDomain);
      // Validate appDomain
      if (appDomain.contains("://")) {
        throw new IllegalArgumentException("appDomain should not have a protocol");
      }
      if (appDomain.startsWith("/") || appDomain.endsWith("/")) {
        throw new IllegalArgumentException("appDomain should not start or end with a slash");
      }
      return this;
    }

    public Builder setRequestTimeout(long timeout, @Nonnull TimeUnit timeUnit) {
      this.requestTimeoutMillis = timeUnit.toMillis(timeout);
      if (this.requestTimeoutMillis <= 0) {
        throw new IllegalArgumentException("timeout must be positive");
      }
      return this;
    }

    public Builder setConnectTimeout(long timeout, @Nonnull TimeUnit timeUnit) {
      this.connectTimeoutMillis = timeUnit.toMillis(timeout);
      if (this.connectTimeoutMillis <= 0) {
        throw new IllegalArgumentException("timeout must be positive");
      }
      return this;
    }

    public SaaSquatchClientOptions build() {
      return new SaaSquatchClientOptions(tenantAlias, appDomain, requestTimeoutMillis,
          connectTimeoutMillis);
    }

  }

}
