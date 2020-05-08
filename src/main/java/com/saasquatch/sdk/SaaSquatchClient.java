package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.InternalUtils.urlEncode;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.concurrent.DefaultThreadFactory;
import org.reactivestreams.Publisher;
import com.saasquatch.sdk.models.User;
import com.saasquatch.sdk.models.UserEventResult;
import com.saasquatch.sdk.models.WidgetUpsertResult;
import io.reactivex.rxjava3.core.Flowable;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Main entry point for SaaSquatch APIs
 *
 * @author sli
 * @see SaaSquatchClient#createForTenant(String)
 */
public final class SaaSquatchClient implements Closeable {

  private final ClientOptions clientOptions;
  private final String protocol;
  private final String clientId;
  private final ExecutorService executor;
  private final OkHttpClient okHttpClient;
  private final CloseableHttpAsyncClient httpAsyncClient;
  private final String userAgent;

  private SaaSquatchClient(@Nonnull ClientOptions clientOptions) {
    this.clientOptions = clientOptions;
    this.protocol = clientOptions.getAppDomain().startsWith("localhost:") ? "http" : "https";
    this.clientId = InternalUtils.randomHexString(8);
    this.executor = Executors.newCachedThreadPool(new InternalThreadFactory(this.clientId));
    final okhttp3.Dispatcher dispatcher = new okhttp3.Dispatcher(this.executor);
    dispatcher.setMaxRequestsPerHost(clientOptions.getMaxConcurrentRequests());
    dispatcher.setMaxRequests(clientOptions.getMaxConcurrentRequests() * 2);
    this.okHttpClient = new OkHttpClient.Builder()
        .dispatcher(dispatcher)
        .callTimeout(clientOptions.getRequestTimeoutMillis(), TimeUnit.MILLISECONDS)
        .connectTimeout(clientOptions.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
        .build();
    this.httpAsyncClient = HttpAsyncClients.custom().disableCookieManagement()
        .setDefaultRequestConfig(RequestConfig.custom()
            .setConnectTimeout(clientOptions.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
            .setResponseTimeout(clientOptions.getRequestTimeoutMillis(), TimeUnit.MILLISECONDS)
            .build())
        .setConnectionManager(PoolingAsyncClientConnectionManagerBuilder.create()
            .setMaxConnPerRoute(100).setMaxConnTotal(200).build())
        .setThreadFactory(new DefaultThreadFactory("", true))
        .build();
    this.userAgent = InternalUtils.buildUserAgent(this.clientId);
  }

  /**
   * Initialize a {@link SaaSquatchClient} with a tenantAlias and default options.
   *
   * @param tenantAlias Your tenantAlias. This will be the default tenantAlias for all your
   *        requests. If you are in a multi-tenant environment, you should be using
   *        {@link SaaSquatchClient#create(ClientOptions)} without a tenantAlias, and then pass the
   *        tenantAlias you want to use in every request via
   *        {@link RequestOptions#setTenantAlias(String)}
   * @see #create(ClientOptions)
   */
  public static SaaSquatchClient createForTenant(@Nonnull String tenantAlias) {
    return new SaaSquatchClient(ClientOptions.newBuilder().setTenantAlias(tenantAlias).build());
  }

  /**
   * Initialize a {@link SaaSquatchClient} with a custom {@link ClientOptions}.
   *
   * @see ClientOptions#newBuilder()
   */
  public static SaaSquatchClient create(@Nonnull ClientOptions clientOptions) {
    return new SaaSquatchClient(Objects.requireNonNull(clientOptions, "clientOptions"));
  }

  @Override
  public void close() {
    this.executor.shutdown();
    try {
      this.httpAsyncClient.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Build a message link for a given user with the specified program and mediums.<br>
   * Note that this method simply builds a URL and does not do any I/O. Depending on the combination
   * of shareMedium and engagementMedium, the URL built may not work. And since this method does not
   * make an API call, the configured {@link AuthMethod} and HTTP headers are ignored.<br>
   * <a href="https://docs.referralsaasquatch.com/features/message-links/">Link to official docs</a>
   */
  public String buildUserMessageLink(@Nonnull String accountId, @Nonnull String userId,
      @Nullable String programId, @Nonnull String shareMedium, @Nullable String engagementMedium,
      @Nullable RequestOptions requestOptions) {
    requireNotBlank(accountId, "accountId");
    requireNotBlank(userId, "userId");
    requireNotBlank(shareMedium, "shareMedium");
    final HttpUrl.Builder urlBuilder = HttpUrl.parse(baseTenantAUrl(requestOptions)
        .append("/message/redirect/").append(urlEncode(shareMedium)).toString())
        .newBuilder();
    urlBuilder.addQueryParameter("accountId", accountId);
    urlBuilder.addQueryParameter("userId", userId);
    if (programId != null) {
      urlBuilder.addQueryParameter("programId", programId);
    }
    if (engagementMedium != null) {
      urlBuilder.addQueryParameter("engagementMedium", engagementMedium);
    }
    return urlBuilder.build().toString();
  }

  public Publisher<GraphQLApiResponse> graphQL(@Nonnull String query,
      @Nullable String operationName, @Nullable Map<String, Object> variables,
      @Nullable RequestOptions requestOptions) {
    final Map<String, Object> body = new HashMap<>();
    body.put("query", requireNotBlank(query, "query"));
    if (operationName != null) {
      body.put("operationName", operationName);
    }
    if (variables != null) {
      body.put("variables", variables);
    }
    final HttpUrl.Builder urlBuilder =
        HttpUrl.parse(baseTenantApiUrl(requestOptions) + "/graphql").newBuilder();
    mutateUrl(urlBuilder, requestOptions);
    final Request.Builder request = new Request.Builder().url(urlBuilder.build());
    mutateRequest(request, requestOptions);
    request.post(InternalRequestBodies.jsonPojo(body));
    return executeRequest(request).map(GraphQLApiResponse::new);
  }

  /**
   * Get a user.<br>
   * By default, the result of the response can be unmarshalled to {@link User}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_get_user">Link to official
   * docs</a>
   */
  public Publisher<MapApiResponse> getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable RequestOptions requestOptions) {
    return _getUser(accountId, userId, null, requestOptions, false).map(MapApiResponse::new);
  }

  /**
   * Render a widget for a user.<br>
   * The response is the widget HTML.
   */
  public Publisher<TextApiResponse> renderWidget(@Nonnull String accountId, @Nonnull String userId,
      @Nullable WidgetType widgetType, @Nullable RequestOptions requestOptions) {
    return _getUser(accountId, userId, widgetType, requestOptions, true).map(TextApiResponse::new);
  }

  private Flowable<Response> _getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable WidgetType widgetType, @Nullable RequestOptions requestOptions,
      boolean widgetRequest) {
    requireNotBlank(accountId, "accountId");
    requireNotBlank(userId, "userId");
    final StringBuilder urlStrBuilder = baseTenantApiUrl(requestOptions)
        .append(widgetRequest ? "/widget" : "/open")
        .append("/account/").append(urlEncode(accountId))
        .append("/user/").append(urlEncode(userId));
    if (widgetRequest) {
      urlStrBuilder.append("/render");
    }
    final HttpUrl.Builder urlBuilder = HttpUrl.parse(urlStrBuilder.toString()).newBuilder();
    mutateUrl(urlBuilder, requestOptions);
    if (widgetType != null) {
      urlBuilder.addQueryParameter("widgetType", widgetType.toString());
    }
    final Request.Builder request = new Request.Builder().url(urlBuilder.build());
    mutateRequest(request, requestOptions);
    request.get();
    return executeRequest(request);
  }

  /**
   * Create or update a user.<br>
   * By default, the result of the response can be unmarshalled to {@link User}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_user_upsert">Link to official
   * docs</a>
   */
  public Publisher<MapApiResponse> userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable RequestOptions requestOptions) {
    return _userUpsert(userInput, null, requestOptions, false).map(MapApiResponse::new);
  }

  /**
   * Create or update a user and render the widget.<br>
   * By default, the result of the response can be unmarshalled to {@link WidgetUpsertResult}.
   */
  public Publisher<MapApiResponse> widgetUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable WidgetType widgetType, @Nullable RequestOptions requestOptions) {
    return _userUpsert(userInput, widgetType, requestOptions, true).map(MapApiResponse::new);
  }

  private Flowable<Response> _userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable WidgetType widgetType, @Nullable RequestOptions requestOptions,
      boolean widgetRequest) {
    final Map<String, Object> body = userInput;
    final String accountId = requireNotBlank((String) body.get("accountId"), "accountId");
    final String userId = requireNotBlank((String) body.get("id"), "id");
    final StringBuilder urlStrBuilder = baseTenantApiUrl(requestOptions)
        .append(widgetRequest ? "/widget" : "/open")
        .append("/account/").append(urlEncode(accountId))
        .append("/user/").append(urlEncode(userId));
    if (widgetRequest) {
      urlStrBuilder.append("/upsert");
    }
    final HttpUrl.Builder urlBuilder = HttpUrl.parse(urlStrBuilder.toString()).newBuilder();
    mutateUrl(urlBuilder, requestOptions);
    if (widgetType != null) {
      urlBuilder.addQueryParameter("widgetType", widgetType.toString());
    }
    final Request.Builder request = new Request.Builder().url(urlBuilder.build());
    mutateRequest(request, requestOptions);
    request.put(InternalRequestBodies.jsonPojo(body));
    return executeRequest(request);
  }

  /**
   * Get a Map of a user's share links<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#lookup-a-users-share-urls">Link to
   * official docs</a>
   */
  public Publisher<MapApiResponse> getUserShareLinks(@Nonnull String accountId,
      @Nonnull String userId, @Nullable String programId, @Nullable String shareMedium,
      @Nullable String engagementMedium, @Nullable RequestOptions requestOptions) {
    // api/v1/:tenantAlias/account/:accountId/user/:userId/shareurls
    requireNotBlank(accountId, "accountId");
    requireNotBlank(userId, "usreId");
    final HttpUrl.Builder urlBuilder = HttpUrl.parse(baseTenantApiUrl(requestOptions)
        .append("/account/").append(urlEncode(accountId))
        .append("/user/").append(urlEncode(userId)).append("/shareurls").toString())
        .newBuilder();
    mutateUrl(urlBuilder, requestOptions);
    if (shareMedium != null) {
      urlBuilder.addQueryParameter("shareMedium", shareMedium);
    }
    if (engagementMedium != null) {
      urlBuilder.addQueryParameter("engagementMedium", engagementMedium);
    }
    final Request.Builder request = new Request.Builder().url(urlBuilder.build());
    mutateRequest(request, requestOptions);
    request.get();
    return executeRequest(request).map(MapApiResponse::new);
  }

  /**
   * Log a user event.<br>
   * By default, the result of the response can be unmarshalled to {@link UserEventResult}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#trackEvent">Link to official docs</a>
   */
  public Publisher<MapApiResponse> logUserEvent(@Nonnull Map<String, Object> userEventInput,
      @Nullable RequestOptions requestOptions) {
    final Map<String, Object> body = userEventInput;
    final String accountId = requireNotBlank((String) body.get("accountId"), "accountId");
    final String userId = requireNotBlank((String) body.get("userId"), "userId");
    final HttpUrl.Builder urlBuilder = HttpUrl.parse(baseTenantApiUrl(requestOptions)
        .append("/open/account/").append(urlEncode(accountId))
        .append("/user/").append(urlEncode(userId)).append("/events").toString())
        .newBuilder();
    mutateUrl(urlBuilder, requestOptions);
    final Request.Builder request = new Request.Builder().url(urlBuilder.build());
    mutateRequest(request, requestOptions);
    request.post(InternalRequestBodies.jsonPojo(body));
    return executeRequest(request).map(MapApiResponse::new);
  }

  /**
   * Apply a referral code.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_apply_code">Link to official
   * docs</a>
   */
  public Publisher<MapApiResponse> applyReferralCode(@Nonnull String accountId,
      @Nonnull String userId, @Nonnull String referralCode,
      @Nullable RequestOptions requestOptions) {
    requireNotBlank(accountId, "accountId");
    requireNotBlank(userId, "userId");
    requireNotBlank(referralCode, "referralCode");
    final HttpUrl.Builder urlBuilder = HttpUrl.parse(baseTenantApiUrl(requestOptions)
        .append("/open/code/").append(urlEncode(referralCode))
        .append("/account/").append(urlEncode(accountId))
        .append("/user/").append(urlEncode(userId)).toString())
        .newBuilder();
    mutateUrl(urlBuilder, requestOptions);
    final Request.Builder request = new Request.Builder().url(urlBuilder.build());
    mutateRequest(request, requestOptions);
    request.post(InternalRequestBodies.jsonString("{}"));
    return executeRequest(request).map(MapApiResponse::new);
  }

  /**
   * All the common url mutations happen here
   */
  private void mutateUrl(@Nonnull HttpUrl.Builder urlBuilder,
      @Nullable RequestOptions requestOptions) {
    if (requestOptions != null) {
      requestOptions.mutateUrl(urlBuilder);
    }
  }

  /**
   * All the common request mutations happen here
   */
  private void mutateRequest(@Nonnull Request.Builder request,
      @Nullable RequestOptions requestOptions) {
    if (requestOptions != null) {
      requestOptions.mutateRequest(request);
    }
    getAuthMethod(requestOptions).mutateRequest(request);
    request.header("User-Agent", userAgent);
  }

  @Nonnull
  private String getTenantAlias(@Nullable RequestOptions requestOptions) {
    String tenantAlias = null;
    if (requestOptions != null) {
      tenantAlias = requestOptions.getTenantAlias();
    }
    if (tenantAlias == null) {
      tenantAlias = this.clientOptions.getTenantAlias();
    }
    return requireNotBlank(tenantAlias, "tenantAlias");
  }

  @Nonnull
  private AuthMethod getAuthMethod(@Nullable RequestOptions requestOptions) {
    AuthMethod authMethod = null;
    if (requestOptions != null) {
      authMethod = requestOptions.getAuthMethod();
    }
    if (authMethod == null) {
      authMethod = this.clientOptions.getAuthMethod();
    }
    return authMethod == null ? AuthMethod.noAuth() : authMethod;
  }

  /**
   * Get the base url with protocol and app domain
   */
  private StringBuilder baseUrl(@Nullable RequestOptions requestOptions) {
    return new StringBuilder(protocol).append("://").append(clientOptions.getAppDomain());
  }

  /**
   * Get the base /api/v1/tenantAlias url
   */
  private StringBuilder baseTenantApiUrl(@Nullable RequestOptions requestOptions) {
    /*
     * Not using okhttp HttpUrl.Builder here because Apache's URIBuilder cannot do append path
     * segment, and we don't want to depend on okhttp too much.
     */
    final String tenantAlias = getTenantAlias(requestOptions);
    return baseUrl(requestOptions).append("/api/v1/").append(urlEncode(tenantAlias));
  }

  /**
   * Get the base /a/tenantAlias url
   */
  private StringBuilder baseTenantAUrl(@Nullable RequestOptions requestOptions) {
    // Not using okhttp HttpUrl.Builder intentionally
    final String tenantAlias = getTenantAlias(requestOptions);
    return baseUrl(requestOptions).append("/a/").append(urlEncode(tenantAlias));
  }

  private Flowable<Response> executeRequest(@Nonnull Request.Builder request) {
    return InternalUtils.executeRequest(okHttpClient, request.build());
  }

}
