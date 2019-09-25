package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal._GsonHolder.gson;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
public final class SaaSquatchClient {

  private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

  private final String appDomain;
  private final String scheme;
  private final OkHttpClient okHttpClient;
  private final String tenantAlias;

  private SaaSquatchClient(@Nullable String tenantAlias) {
    this.tenantAlias = tenantAlias;
    this.appDomain =
        System.getProperty("com.saasquatch.sdk.appDomain", "app.referralsaasquatch.com");
    this.scheme = appDomain.startsWith("localhost:") ? "http" : "https";
    this.okHttpClient = new OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build();
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

  public CompletionStage<SaaSquatchGraphQLResponse> graphQL(@Nonnull String query,
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
    return executeRequest(requestBuilder.build()).thenApply(SaaSquatchGraphQLResponse::new);
  }

  public CompletionStage<SaaSquatchMapResponse> getUser(@Nonnull String accountId,
      @Nonnull String userId, SaaSquatchRequestOptions requestOptions) {
    return _getUser(accountId, userId, requestOptions, false).thenApply(SaaSquatchMapResponse::new);
  }

  public CompletionStage<SaaSquatchTextResponse> renderWidget(@Nonnull String accountId,
      @Nonnull String userId, SaaSquatchRequestOptions requestOptions) {
    return _getUser(accountId, userId, requestOptions, true).thenApply(SaaSquatchTextResponse::new);
  }

  private CompletionStage<Response> _getUser(@Nonnull String accountId, @Nonnull String userId,
      SaaSquatchRequestOptions requestOptions, boolean widgetRequest) {
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
    return executeRequest(requestBuilder.build());
  }

  public CompletionStage<SaaSquatchMapResponse> userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    return _userUpsert(userInput, requestOptions, false).thenApply(SaaSquatchMapResponse::new);
  }

  public CompletionStage<SaaSquatchMapResponse> widgetUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    return _userUpsert(userInput, requestOptions, true).thenApply(SaaSquatchMapResponse::new);
  }

  private CompletionStage<Response> _userUpsert(
      @Nonnull Map<String, Object> userInput, @Nullable SaaSquatchRequestOptions requestOptions,
      boolean widgetRequest) {
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
    return executeRequest(requestBuilder.build());
  }

  public CompletionStage<SaaSquatchMapResponse> logUserEvent(
      @Nonnull Map<String, Object> userEventInput,
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
    return executeRequest(requestBuilder.build()).thenApply(SaaSquatchMapResponse::new);
  }

  public CompletionStage<SaaSquatchMapResponse> applyReferralCode(@Nonnull String accountId,
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
    return executeRequest(requestBuilder.build()).thenApply(SaaSquatchMapResponse::new);
  }

  private HttpUrl.Builder baseApiUrl(@Nullable SaaSquatchRequestOptions requestOptions) {
    final String tenantAliasToUse = Optional.ofNullable(requestOptions)
        .map(SaaSquatchRequestOptions::getTenantAlias)
        .orElse(tenantAlias);
    Objects.requireNonNull(tenantAliasToUse, "tenantAlias missing");
    return new HttpUrl.Builder().scheme(scheme).host(appDomain)
        .addPathSegment("api")
        .addPathSegment("v1")
        .addPathSegment(tenantAliasToUse);
  }

  private CompletionStage<Response> executeRequest(Request request) {
    final CompletableFuture<Response> responsePromise = new CompletableFuture<>();
    okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
      @Override
      public void onResponse(Call call, Response resp) throws IOException {
        responsePromise.complete(resp);
      }

      @Override
      public void onFailure(Call call, IOException e) {
        responsePromise.completeExceptionally(e);
      }
    });
    return responsePromise;
  }

  private static RequestBody jsonRequestBody(Object bodyObj) {
    return RequestBody.create(gson.toJson(bodyObj).getBytes(UTF_8), JSON_MEDIA_TYPE);
  }

}
