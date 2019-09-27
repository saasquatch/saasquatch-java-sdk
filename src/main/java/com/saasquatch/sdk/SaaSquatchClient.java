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

  private final SaaSquatchClientOptions clientOptions;
  private final String scheme;
  private final ExecutorService executor;
  private final OkHttpClient okHttpClient;
  private final String userAgent;

  private SaaSquatchClient(@Nonnull SaaSquatchClientOptions clientOptions) {
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
   *        {@link SaaSquatchClient#create(SaaSquatchClientOptions)} without a tenantAlias, and then
   *        pass the tenantAlias you want to use in every request via
   *        {@link SaaSquatchRequestOptions#setTenantAlias(String)}
   * @see #create(SaaSquatchClientOptions)
   */
  public static SaaSquatchClient createForTenant(@Nonnull String tenantAlias) {
    return new SaaSquatchClient(
        SaaSquatchClientOptions.newBuilder().setTenantAlias(tenantAlias).build());
  }

  /**
   * Initialize a {@link SaaSquatchClient} with a custom {@link SaaSquatchClientOptions}.
   *
   * @see SaaSquatchClientOptions#newBuilder()
   */
  public static SaaSquatchClient create(@Nonnull SaaSquatchClientOptions clientOptions) {
    return new SaaSquatchClient(Objects.requireNonNull(clientOptions, "clientOptions"));
  }

  @Override
  public void close() {
    this.executor.shutdown();
  }

  public Publisher<SaaSquatchGraphQLResponse> graphQL(@Nonnull String query,
      @Nullable String operationName, @Nullable Map<String, Object> variables,
      @Nullable SaaSquatchRequestOptions requestOptions) {
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
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).post(InternalRequestBodies.jsonPojo(body));
    return executeRequest(requestBuilder).map(SaaSquatchGraphQLResponse::new);
  }

  /**
   * Get a user.<br>
   * By default, the result of the response can be unmarshalled to {@link User}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_get_user">Link to official
   * docs</a>
   */
  public Publisher<SaaSquatchMapResponse> getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    return _getUser(accountId, userId, requestOptions, false).map(SaaSquatchMapResponse::new);
  }

  /**
   * Render a widget for a user.<br>
   * The response is the widget HTML.
   */
  public Publisher<SaaSquatchTextResponse> renderWidget(@Nonnull String accountId,
      @Nonnull String userId, @Nullable SaaSquatchRequestOptions requestOptions) {
    return _getUser(accountId, userId, requestOptions, true).map(SaaSquatchTextResponse::new);
  }

  private Flowable<Response> _getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable SaaSquatchRequestOptions requestOptions, boolean widgetRequest) {
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
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
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
  public Publisher<SaaSquatchMapResponse> userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    return _userUpsert(userInput, requestOptions, false).map(SaaSquatchMapResponse::new);
  }

  /**
   * Create or update a user and render the widget.<br>
   * By default, the result of the response can be unmarshalled to {@link WidgetUpsertResult}.
   */
  public Publisher<SaaSquatchMapResponse> widgetUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    return _userUpsert(userInput, requestOptions, true).map(SaaSquatchMapResponse::new);
  }

  private Flowable<Response> _userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable SaaSquatchRequestOptions requestOptions, boolean widgetRequest) {
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
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).put(InternalRequestBodies.jsonPojo(body));
    return executeRequest(requestBuilder);
  }

  /**
   * Log a user event.<br>
   * By default, the result of the response can be unmarshalled to {@link UserEventResult}.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#trackEvent">Link to official docs</a>
   */
  public Publisher<SaaSquatchMapResponse> logUserEvent(@Nonnull Map<String, Object> userEventInput,
      @Nullable SaaSquatchRequestOptions requestOptions) {
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
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).post(InternalRequestBodies.jsonPojo(body));
    return executeRequest(requestBuilder).map(SaaSquatchMapResponse::new);
  }

  /**
   * Apply a referral code.<br>
   * <a href="https://docs.referralsaasquatch.com/api/methods/#open_apply_code">Link to official
   * docs</a>
   */
  public Publisher<SaaSquatchMapResponse> applyReferralCode(@Nonnull String accountId,
      @Nonnull String userId, @Nonnull String referralCode,
      @Nullable SaaSquatchRequestOptions requestOptions) {
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
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).post(InternalRequestBodies.jsonString("{}"));
    return executeRequest(requestBuilder).map(SaaSquatchMapResponse::new);
  }

  @Nonnull
  private String getTenantAlias(@Nullable SaaSquatchRequestOptions requestOptions) {
    String tenantAlias = null;
    if (requestOptions != null) {
      tenantAlias = requestOptions.getTenantAlias();
    }
    if (tenantAlias == null) {
      tenantAlias = this.clientOptions.getTenantAlias();
    }
    return Objects.requireNonNull(tenantAlias, "tenantAlias missing");
  }

  private HttpUrl.Builder baseApiUrl(@Nullable SaaSquatchRequestOptions requestOptions) {
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
