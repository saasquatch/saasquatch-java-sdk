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
    private static final long MAX_REQUEST_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(1);
    private static final long MAX_CONNECT_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(30);

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
      Objects.requireNonNull(appDomain, "appDomain");
      // Validate appDomain
      if (appDomain.contains("://")) {
        throw new IllegalArgumentException("appDomain should not have a protocol");
      }
      if (appDomain.startsWith("/") || appDomain.endsWith("/")) {
        throw new IllegalArgumentException("appDomain should not start or end with a slash");
      }
      this.appDomain = appDomain;
      return this;
    }

    public Builder setRequestTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      final long millis = timeUnit.toMillis(duration);
      if (millis <= 0) {
        throw new IllegalArgumentException("Invalid timeout");
      }
      if (millis > MAX_REQUEST_TIMEOUT_MILLIS) {
        throw new IllegalArgumentException("Timeout too large");
      }
      this.requestTimeoutMillis = millis;
      return this;
    }

    public Builder setConnectTimeout(long duration, @Nonnull TimeUnit timeUnit) {
      final long millis = timeUnit.toMillis(duration);
      if (millis <= 0) {
        throw new IllegalArgumentException("Invalid timeout");
      }
      if (millis > MAX_CONNECT_TIMEOUT_MILLIS) {
        throw new IllegalArgumentException("Timeout too large");
      }
      this.connectTimeoutMillis = millis;
      return this;
    }

    /**
     * Build an immutable {@link SaaSquatchClientOptions}
     */
    public SaaSquatchClientOptions build() {
      return new SaaSquatchClientOptions(tenantAlias, appDomain, requestTimeoutMillis,
          connectTimeoutMillis);
    }

  }

}
