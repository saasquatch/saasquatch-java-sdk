package saasquatch.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.google.gson.Gson;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public final class SaaSquatchClient {

  static final Gson gson = new Gson();

  private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");

  private final String appDomain;
  private final String scheme;
  private final OkHttpClient okHttpClient;

  public SaaSquatchClient() {
    this.appDomain = System.getProperty("saasquatch.sdk.appDomain", "app.referralsaasquatch.com");
    this.scheme = appDomain.startsWith("localhost:") ? "http" : "https";
    this.okHttpClient = new OkHttpClient.Builder()
        .callTimeout(15, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .build();
    this.onStart();
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

  public CompletionStage<SaaSquatchGraphQLResponse> graphQL(@Nonnull String tenantAlias,
      @Nonnull String query, @Nullable String operationName,
      @Nullable Map<String, Object> variables, @Nullable SaaSquatchRequestOptions requestOptions) {
    final Map<String, Object> body = new HashMap<>();
    body.put("query", Objects.requireNonNull(query));
    if (operationName != null) {
      body.put("operationName", operationName);
    }
    if (variables != null) {
      body.put("variables", variables);
    }
    final HttpUrl.Builder urlBuilder = baseApiUrl(tenantAlias).addPathSegment("graphql");
    final Request.Builder requestBuilder = new Request.Builder();
    if (requestOptions != null) {
      requestOptions.mutateRequest(requestBuilder, urlBuilder);
    }
    requestBuilder.url(urlBuilder.build())
        .post(jsonRequestBody(gson.toJson(body)));
    return executeRequest(requestBuilder.build())
        .thenApply(SaaSquatchGraphQLResponse::new);
  }

  public CompletionStage<SaaSquatchObjectResponse> getUser(@Nonnull String tenantAlias,
      @Nonnull String accountId, @Nonnull String userId, SaaSquatchRequestOptions requestOptions) {
    return _getUser(tenantAlias, accountId, userId, requestOptions, false)
        .thenApply(SaaSquatchObjectResponse::new);
  }

  public CompletionStage<SaaSquatchTextResponse> renderWidget(@Nonnull String tenantAlias,
      @Nonnull String accountId, @Nonnull String userId, SaaSquatchRequestOptions requestOptions) {
    return _getUser(tenantAlias, accountId, userId, requestOptions, true)
        .thenApply(SaaSquatchTextResponse::new);
  }

  private CompletionStage<Response> _getUser(@Nonnull String tenantAlias, @Nonnull String accountId,
      @Nonnull String userId, SaaSquatchRequestOptions requestOptions, boolean widgetRequest) {
    Objects.requireNonNull(accountId);
    Objects.requireNonNull(userId);
    final HttpUrl.Builder urlBuilder = baseApiUrl(tenantAlias)
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

  public CompletionStage<SaaSquatchObjectResponse> userUpsert(@Nonnull String tenantAlias,
      @Nonnull Map<String, Object> userInput, @Nullable SaaSquatchRequestOptions requestOptions) {
    return _userUpsert(tenantAlias, userInput, requestOptions, false);
  }

  public CompletionStage<SaaSquatchObjectResponse> widgetUpsert(@Nonnull String tenantAlias,
      @Nonnull Map<String, Object> userInput, @Nullable SaaSquatchRequestOptions requestOptions) {
    return _userUpsert(tenantAlias, userInput, requestOptions, true);
  }

  private CompletionStage<SaaSquatchObjectResponse> _userUpsert(@Nonnull String tenantAlias,
      @Nonnull Map<String, Object> userInput, @Nullable SaaSquatchRequestOptions requestOptions,
      boolean widgetRequest) {
    final Map<String, Object> body = userInput;
    final String accountId =
        Objects.requireNonNull((String) body.get("accountId"), "accountId missing");
    final String userId = Objects.requireNonNull((String) body.get("id"), "id missing");
    final HttpUrl.Builder urlBuilder = baseApiUrl(tenantAlias)
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
    requestBuilder.url(urlBuilder.build())
        .put(jsonRequestBody(gson.toJson(body)));
    return executeRequest(requestBuilder.build())
        .thenApply(SaaSquatchObjectResponse::new);
  }

  public CompletionStage<SaaSquatchObjectResponse> logUserEvent(@Nonnull String tenantAlias,
      @Nonnull Map<String, Object> userEventInput,
      @Nullable SaaSquatchRequestOptions requestOptions) {
    final Map<String, Object> body = userEventInput;
    final String accountId =
        Objects.requireNonNull((String) body.get("accountId"), "accountId missing");
    final String userId = Objects.requireNonNull((String) body.get("userId"), "userId missing");
    final HttpUrl.Builder urlBuilder = baseApiUrl(tenantAlias)
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
    requestBuilder.url(urlBuilder.build())
        .post(jsonRequestBody(gson.toJson(body)));
    return executeRequest(requestBuilder.build())
        .thenApply(SaaSquatchObjectResponse::new);
  }

  private HttpUrl.Builder baseApiUrl(@Nonnull String tenantAlias) {
    Objects.requireNonNull(tenantAlias);
    return new HttpUrl.Builder().scheme(scheme).host(appDomain)
        .addPathSegment("api")
        .addPathSegment("v1")
        .addPathSegment(tenantAlias);
  }

  private CompletionStage<Response> executeRequest(Request request) {
    final CompletableFuture<Response> respPromise = new CompletableFuture<>();
    okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
      @Override
      public void onResponse(Call call, Response resp) throws IOException {
        respPromise.complete(resp);
      }

      @Override
      public void onFailure(Call call, IOException e) {
        respPromise.completeExceptionally(e);
      }
    });
    return respPromise;
  }

  public static void main(String[] args) throws IOException {
    HashMap<Object, Object> m = new HashMap<>();
    m.put("1", 2);
    try (StringWriter stringWriter = new StringWriter()) {
    }
  }

  private static RequestBody jsonRequestBody(String jsonString) {
    return RequestBody.create(jsonString.getBytes(UTF_8), JSON_MEDIA_TYPE);
  }

}
