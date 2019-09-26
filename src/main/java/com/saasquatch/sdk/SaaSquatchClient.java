package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalGsonHolder.gson;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.reactivestreams.Publisher;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Main entry point for SaaSquatch APIs
 *
 * @author sli
 * @see SaaSquatchClient#create(String)
 */
public final class SaaSquatchClient implements Closeable {

  private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

  private final String tenantAlias;
  private final String appDomain;
  private final String scheme;
  private final ExecutorService dispatcherExecutor;
  private final OkHttpClient okHttpClient;
  private final String userAgent;

  private SaaSquatchClient(@Nullable String tenantAlias) {
    this.tenantAlias = tenantAlias;
    this.appDomain =
        System.getProperty("com.saasquatch.sdk.appDomain", "app.referralsaasquatch.com");
    this.scheme = appDomain.startsWith("localhost:") ? "http" : "https";
    this.dispatcherExecutor = newDispatcherExecutor();
    this.okHttpClient = new OkHttpClient.Builder()
        .dispatcher(new okhttp3.Dispatcher(this.dispatcherExecutor))
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build();
    this.userAgent = buildUserAgent();
    this.onStart();
  }

  /**
   * Initialize a {@link SaaSquatchClient} with an optional tenantAlias.
   *
   * @param tenantAlias Your tenantAlias. This will be the default tenantAlias for all your
   *        requests. If you are in a multi-tenant environment, you can initialize a
   *        {@link SaaSquatchClient} with a null tenantAlias, and then pass in your tenantAlias in
   *        every request via {@link SaaSquatchRequestOptions#setTenantAlias(String)}
   */
  public static SaaSquatchClient create(@Nullable String tenantAlias) {
    return new SaaSquatchClient(tenantAlias);
  }

  private void onStart() {
    // Validate appDomain
    if (appDomain.contains("://")) {
      throw new RuntimeException("appDomain should not have a protocol");
    }
    if (appDomain.startsWith("/") || appDomain.endsWith("/")) {
      throw new RuntimeException("appDomain should not start or end with a slash");
    }
  }

  @Override
  public void close() {
    this.dispatcherExecutor.shutdown();
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
    final HttpUrl.Builder urlBuilder = baseApiUrl(requestOptions).addPathSegment("graphql");
    final Request.Builder requestBuilder = new Request.Builder();
    if (requestOptions != null) {
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build()).post(jsonRequestBody(body));
    return executeRequest(requestBuilder).map(SaaSquatchGraphQLResponse::new);
  }

  public Publisher<SaaSquatchMapResponse> getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    return _getUser(accountId, userId, requestOptions, false).map(SaaSquatchMapResponse::new);
  }

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

  public Publisher<SaaSquatchMapResponse> userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    return _userUpsert(userInput, requestOptions, false).map(SaaSquatchMapResponse::new);
  }

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
    requestBuilder.url(urlBuilder.build()).put(jsonRequestBody(body));
    return executeRequest(requestBuilder);
  }

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
    requestBuilder.url(urlBuilder.build()).post(jsonRequestBody(body));
    return executeRequest(requestBuilder).map(SaaSquatchMapResponse::new);
  }

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
    requestBuilder.url(urlBuilder.build()).post(jsonRequestBody(Collections.emptyMap()));
    return executeRequest(requestBuilder).map(SaaSquatchMapResponse::new);
  }

  @Nullable
  private String getTenantAlias(@Nullable SaaSquatchRequestOptions requestOptions) {
    String tenantAliasToUse = null;
    if (requestOptions != null) {
      tenantAliasToUse = requestOptions.getTenantAlias();
    }
    if (tenantAliasToUse == null) {
      tenantAliasToUse = this.tenantAlias;
    }
    return tenantAliasToUse;
  }

  private HttpUrl.Builder baseApiUrl(@Nullable SaaSquatchRequestOptions requestOptions) {
    final String tenantAliasToUse = getTenantAlias(requestOptions);
    Objects.requireNonNull(tenantAliasToUse, "tenantAlias missing");
    return new HttpUrl.Builder().scheme(scheme).host(appDomain)
        .addPathSegment("api")
        .addPathSegment("v1")
        .addPathSegment(tenantAliasToUse);
  }

  private Flowable<Response> executeRequest(Request.Builder requestBuilder) {
    final Request request = requestBuilder.header("User-Agent", userAgent).build();
    return Single.<Response>create(emitter -> {
      okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
        @Override
        public void onResponse(Call call, Response resp) throws IOException {
          emitter.onSuccess(resp);
        }

        @Override
        public void onFailure(Call call, IOException e) {
          emitter.onError(e);
        }
      });
    }).toFlowable();
  }

  private static RequestBody jsonRequestBody(Object bodyObj) {
    return RequestBody.create(gson.toJson(bodyObj).getBytes(UTF_8), JSON_MEDIA_TYPE);
  }

  private static ExecutorService newDispatcherExecutor() {
    return Executors.newCachedThreadPool(InternalThreadFactory.INSTANCE);
  }

  private static String buildUserAgent() {
    final String javaVersion = System.getProperty("java.version");
    return "SaaSquatch SDK; "
        + (javaVersion == null ? "Unknown Java version" : "Java " + javaVersion);
  }

}
