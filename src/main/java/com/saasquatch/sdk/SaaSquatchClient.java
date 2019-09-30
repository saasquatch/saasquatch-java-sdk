package com.saasquatch.sdk;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.reactivestreams.Publisher;
import com.saasquatch.sdk.models.User;
import com.saasquatch.sdk.models.UserEventResult;
import com.saasquatch.sdk.models.WidgetUpsertResult;
import io.reactivex.Flowable;
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
  private final String scheme;
  private final ExecutorService executor;
  private final OkHttpClient okHttpClient;
  private final String userAgent;

  private SaaSquatchClient(@Nonnull ClientOptions clientOptions) {
    this.clientOptions = clientOptions;
    this.scheme = clientOptions.getAppDomain().startsWith("localhost:") ? "http" : "https";
    this.executor = Executors.newCachedThreadPool(InternalThreadFactory.INSTANCE);
    this.okHttpClient = new OkHttpClient.Builder()
        .dispatcher(new okhttp3.Dispatcher(this.executor))
        .callTimeout(clientOptions.getRequestTimeoutMillis(), TimeUnit.MILLISECONDS)
        .connectTimeout(clientOptions.getConnectTimeoutMillis(), TimeUnit.MILLISECONDS)
        .build();
    this.userAgent = InternalUtils.buildUserAgent();
  }

  /**
   * Initialize a {@link SaaSquatchClient} with a tenantAlias and default options.
   *
   * @param tenantAlias Your tenantAlias. This will be the default tenantAlias for all your
   *        requests. If you are in a multi-tenant environment, you should be using
   *        {@link SaaSquatchClient#create(ClientOptions)} without a tenantAlias, and then
   *        pass the tenantAlias you want to use in every request via
   *        {@link RequestOptions#setTenantAlias(String)}
   * @see #create(ClientOptions)
   */
  public static SaaSquatchClient createForTenant(@Nonnull String tenantAlias) {
    return new SaaSquatchClient(
        ClientOptions.newBuilder().setTenantAlias(tenantAlias).build());
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
  }

  public Publisher<GraphQLApiResponse> graphQL(@Nonnull String query,
      @Nullable String operationName, @Nullable Map<String, Object> variables,
      @Nullable RequestOptions requestOptions) {
    final Map<String, Object> body = new HashMap<>();
    body.put("query", Objects.requireNonNull(query, "query"));
    if (operationName != null) {
      body.put("operationName", operationName);
    }
    if (variables != null) {
      body.put("variables", variables);
    }
    final HttpUrl.Builder urlBuilder = baseApiUrl(requestOptions)
        .addPathSegment("graphql");
    final Request.Builder requestBuilder = new Request.Builder();
    if (requestOptions != null) {
      mutateRequest(requestOptions, requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).post(InternalRequestBodies.jsonPojo(body));
    return executeRequest(requestBuilder).map(GraphQLApiResponse::new);
  }

  /**
   * Get a user.<br>
   * By default, the result of the response can be unmarshalled to {@link User}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_get_user">Link to official
   * docs</a>
   */
  public Publisher<MapApiResponse> getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable RequestOptions requestOptions) {
    return _getUser(accountId, userId, requestOptions, false).map(MapApiResponse::new);
  }

  /**
   * Render a widget for a user.<br>
   * The response is the widget HTML.
   */
  public Publisher<TextApiResponse> renderWidget(@Nonnull String accountId,
      @Nonnull String userId, @Nullable RequestOptions requestOptions) {
    return _getUser(accountId, userId, requestOptions, true).map(TextApiResponse::new);
  }

  private Flowable<Response> _getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable RequestOptions requestOptions, boolean widgetRequest) {
    Objects.requireNonNull(accountId, "accountId");
    Objects.requireNonNull(userId, "userId");
    final HttpUrl.Builder urlBuilder = baseApiUrl(requestOptions)
        .addPathSegment(widgetRequest ? "widget" : "open")
        .addPathSegment("account")
        .addPathSegment(accountId)
        .addPathSegment("user")
        .addPathSegment(userId);
    if (widgetRequest) {
      urlBuilder.addPathSegment("render");
    }
    final Request.Builder requestBuilder = new Request.Builder();
    if (requestOptions != null) {
      mutateRequest(requestOptions, requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).get();
    return executeRequest(requestBuilder);
  }

  /**
   * Create or update a user.<br>
   * By default, the result of the response can be unmarshalled to {@link User}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_user_upsert">Link to official
   * docs</a>
   */
  public Publisher<MapApiResponse> userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable RequestOptions requestOptions) {
    return _userUpsert(userInput, requestOptions, false).map(MapApiResponse::new);
  }

  /**
   * Create or update a user and render the widget.<br>
   * By default, the result of the response can be unmarshalled to {@link WidgetUpsertResult}.
   */
  public Publisher<MapApiResponse> widgetUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable RequestOptions requestOptions) {
    return _userUpsert(userInput, requestOptions, true).map(MapApiResponse::new);
  }

  private Flowable<Response> _userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable RequestOptions requestOptions, boolean widgetRequest) {
    final Map<String, Object> body = userInput;
    final String accountId =
        Objects.requireNonNull((String) body.get("accountId"), "accountId missing");
    final String userId = Objects.requireNonNull((String) body.get("id"), "id missing");
    final HttpUrl.Builder urlBuilder = baseApiUrl(requestOptions)
        .addPathSegment(widgetRequest ? "widget" : "open")
        .addPathSegment("account")
        .addPathSegment(accountId)
        .addPathSegment("user")
        .addPathSegment(userId);
    if (widgetRequest) {
      urlBuilder.addPathSegment("upsert");
    }
    final Request.Builder requestBuilder = new Request.Builder();
    if (requestOptions != null) {
      mutateRequest(requestOptions, requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).put(InternalRequestBodies.jsonPojo(body));
    return executeRequest(requestBuilder);
  }

  /**
   * Log a user event.<br>
   * By default, the result of the response can be unmarshalled to {@link UserEventResult}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#trackEvent">Link to official docs</a>
   */
  public Publisher<MapApiResponse> logUserEvent(@Nonnull Map<String, Object> userEventInput,
      @Nullable RequestOptions requestOptions) {
    final Map<String, Object> body = userEventInput;
    final String accountId =
        Objects.requireNonNull((String) body.get("accountId"), "accountId missing");
    final String userId = Objects.requireNonNull((String) body.get("userId"), "userId missing");
    final HttpUrl.Builder urlBuilder = baseApiUrl(requestOptions)
        .addPathSegment("open")
        .addPathSegment("account")
        .addPathSegment(accountId)
        .addPathSegment("user")
        .addPathSegment(userId)
        .addPathSegment("events");
    final Request.Builder requestBuilder = new Request.Builder();
    if (requestOptions != null) {
      mutateRequest(requestOptions, requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).post(InternalRequestBodies.jsonPojo(body));
    return executeRequest(requestBuilder).map(MapApiResponse::new);
  }

  /**
   * Apply a referral code.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_apply_code">Link to official
   * docs</a>
   */
  public Publisher<MapApiResponse> applyReferralCode(@Nonnull String accountId,
      @Nonnull String userId, @Nonnull String referralCode,
      @Nullable RequestOptions requestOptions) {
    Objects.requireNonNull(accountId, "accountId");
    Objects.requireNonNull(userId, "userId");
    Objects.requireNonNull(referralCode, "referralCode");
    final HttpUrl.Builder urlBuilder = baseApiUrl(requestOptions)
        .addPathSegment("open")
        .addPathSegment("code")
        .addPathSegment(referralCode)
        .addPathSegment("account")
        .addPathSegment(accountId)
        .addPathSegment("user")
        .addPathSegment(userId);
    final Request.Builder requestBuilder = new Request.Builder();
    if (requestOptions != null) {
      mutateRequest(requestOptions, requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).post(InternalRequestBodies.jsonString("{}"));
    return executeRequest(requestBuilder).map(MapApiResponse::new);
  }

  private void mutateRequest(@Nullable RequestOptions requestOptions,
      Request.Builder requestBuilder, HttpUrl.Builder urlBuilder) {
    if (requestOptions != null) {
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
    }
    getAuthMethod(requestOptions).mutateRequest(requestBuilder);
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
    return Objects.requireNonNull(tenantAlias, "tenantAlias missing");
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

  private HttpUrl.Builder baseApiUrl(@Nullable RequestOptions requestOptions) {
    final String tenantAlias = getTenantAlias(requestOptions);
    return new HttpUrl.Builder().scheme(scheme).host(clientOptions.getAppDomain())
        .addPathSegment("api")
        .addPathSegment("v1")
        .addPathSegment(tenantAlias);
  }

  private Flowable<Response> executeRequest(Request.Builder requestBuilder) {
    final Request request = requestBuilder.header("User-Agent", userAgent).build();
    return Flowable.fromPublisher(InternalUtils.executeRequest(okHttpClient, request));
  }

}
