package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.InternalUtils.defaultIfNull;
import static com.saasquatch.sdk.internal.InternalUtils.getJwtPayload;
import static com.saasquatch.sdk.internal.InternalUtils.getNestedMapValue;
import static com.saasquatch.sdk.internal.InternalUtils.getUserIdInputFromUserJwt;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.json.GsonUtils.gson;

import com.saasquatch.sdk.auth.AuthMethod;
import com.saasquatch.sdk.exceptions.SaaSquatchApiException;
import com.saasquatch.sdk.exceptions.SaaSquatchIOException;
import com.saasquatch.sdk.exceptions.SaaSquatchUnhandledApiException;
import com.saasquatch.sdk.http.Client5SaaSquatchHttpResponse;
import com.saasquatch.sdk.http.SaaSquatchHttpResponse;
import com.saasquatch.sdk.input.GetUserLinkInput;
import com.saasquatch.sdk.input.GraphQLInput;
import com.saasquatch.sdk.input.RenderWidgetInput;
import com.saasquatch.sdk.input.UserEventInput;
import com.saasquatch.sdk.input.UserIdInput;
import com.saasquatch.sdk.input.UserInput;
import com.saasquatch.sdk.input.WidgetType;
import com.saasquatch.sdk.input.WidgetUpsertInput;
import com.saasquatch.sdk.internal.InternalUtils;
import com.saasquatch.sdk.output.ApiError;
import com.saasquatch.sdk.output.GraphQLApiResponse;
import com.saasquatch.sdk.output.GraphQLResult;
import com.saasquatch.sdk.output.JsonObjectApiResponse;
import com.saasquatch.sdk.output.TextApiResponse;
import io.reactivex.rxjava3.core.Flowable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.net.URIBuilder;
import org.reactivestreams.Publisher;

final class SaaSquatchClientImpl implements SaaSquatchClient {

  private final ClientOptions clientOptions;
  private final String scheme;
  private final String clientId;
  private final CloseableHttpAsyncClient httpAsyncClient;

  SaaSquatchClientImpl(@Nonnull ClientOptions clientOptions) {
    this.clientOptions = clientOptions;
    this.scheme = clientOptions.getAppDomain().startsWith("localhost:") ? "http" : "https";
    this.clientId = InternalUtils.randomHexString(8);
    this.httpAsyncClient = HttpAsyncClients.custom().disableCookieManagement()
        .setConnectionManager(PoolingAsyncClientConnectionManagerBuilder.create()
            .setMaxConnPerRoute(clientOptions.getMaxConcurrentRequests())
            .setMaxConnTotal(clientOptions.getMaxConcurrentRequests())
            .build())
        .setUserAgent(InternalUtils.buildUserAgent(this.clientId))
        .build();
    this.httpAsyncClient.start();
  }

  @Override
  public void close() throws IOException {
    this.httpAsyncClient.close();
  }

  @Nonnull
  @Override
  public String buildUserMessageLink(@Nonnull GetUserLinkInput getUserLinkInput,
      @Nullable RequestOptions requestOptions) {
    Objects.requireNonNull(getUserLinkInput, "getUserLinkInput");
    final URIBuilder uriBuilder = baseUriBuilder(requestOptions);
    final List<String> pathSegments = baseTenantAPathSegments(requestOptions);
    Collections.addAll(pathSegments, "message", "redirect",
        requireNotBlank(getUserLinkInput.getShareMedium(), "shareMedium"));
    mutateUri(uriBuilder, pathSegments, requestOptions);
    uriBuilder.addParameter("accountId", getUserLinkInput.getAccountId());
    uriBuilder.addParameter("userId", getUserLinkInput.getUserId());
    if (getUserLinkInput.getProgramId() != null) {
      uriBuilder.addParameter("programId", getUserLinkInput.getProgramId());
    }
    if (getUserLinkInput.getEngagementMedium() != null) {
      uriBuilder.addParameter("engagementMedium", getUserLinkInput.getEngagementMedium());
    }
    return uriBuilder.toString();
  }

  @Override
  public Publisher<GraphQLApiResponse> graphQL(@Nonnull GraphQLInput graphQLInput,
      @Nullable RequestOptions requestOptions) {
    return _graphQL(graphQLInput, null, requestOptions);
  }

  private Publisher<GraphQLApiResponse> _graphQL(@Nonnull GraphQLInput graphQLInput, String userJwt,
      @Nullable RequestOptions requestOptions) {
    Objects.requireNonNull(graphQLInput, "graphQLInput");
    final URIBuilder uriBuilder = baseUriBuilder(requestOptions);
    final List<String> pathSegments = baseTenantApiPathSegments(requestOptions);
    pathSegments.add("graphql");
    mutateUri(uriBuilder, pathSegments, requestOptions);
    final SimpleHttpRequest request = SimpleHttpRequests.post(uriBuilder.toString());
    mutateRequest(request, requestOptions);
    if (userJwt != null) {
      AuthMethod.ofJwt(userJwt).mutateRequest(request);
    }
    setJsonPojoBody(request, graphQLInput);
    return executeRequest(request).map(GraphQLApiResponse::new);
  }

  @Override
  public Publisher<JsonObjectApiResponse> getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable RequestOptions requestOptions) {
    return _getUser(accountId, userId, null, null, requestOptions, false)
        .map(JsonObjectApiResponse::new);
  }

  @Override
  public Publisher<JsonObjectApiResponse> getUserWithUserJwt(@Nonnull String userJwt,
      @Nullable RequestOptions requestOptions) {
    requireNotBlank(userJwt, "userJwt");
    final UserIdInput userIdInput = getUserIdInputFromUserJwt(userJwt);
    return _getUser(userIdInput.getAccountId(), userIdInput.getId(), userJwt, null, requestOptions,
        false)
        .map(JsonObjectApiResponse::new);
  }

  private Flowable<SaaSquatchHttpResponse> _getUser(@Nonnull String accountId,
      @Nonnull String userId, @Nullable String userJwt, @Nullable WidgetType widgetType,
      @Nullable RequestOptions requestOptions, boolean widgetRequest) {
    final URIBuilder uriBuilder = baseUriBuilder(requestOptions);
    final List<String> pathSegments = baseTenantApiPathSegments(requestOptions);
    Collections.addAll(pathSegments, widgetRequest ? "widget" : "open", "account",
        requireNotBlank(accountId, "accountId"), "user", requireNotBlank(userId, "userId"));
    if (widgetRequest) {
      pathSegments.add("render");
    }
    mutateUri(uriBuilder, pathSegments, requestOptions);
    if (widgetType != null) {
      uriBuilder.addParameter("widgetType", widgetType.getWidgetType());
    }
    final SimpleHttpRequest request = SimpleHttpRequests.get(uriBuilder.toString());
    mutateRequest(request, requestOptions);
    if (userJwt != null) {
      AuthMethod.ofJwt(userJwt).mutateRequest(request);
    }
    return executeRequest(request);
  }

  @Override
  public Publisher<TextApiResponse> renderWidget(
      @Nonnull RenderWidgetInput renderWidgetInput, @Nullable RequestOptions requestOptions) {
    Objects.requireNonNull(renderWidgetInput, "renderWidgetInput");
    final String query = "query renderWidget(\n"
        + "  $user: UserIdInput\n"
        + "  $widgetType: WidgetType\n"
        + "  $engagementMedium: UserEngagementMedium\n"
        + "  $locale: RSLocale\n"
        + ") {\n"
        + "  renderWidget(\n"
        + "    user: $user\n"
        + "    widgetType: $widgetType\n"
        + "    engagementMedium: $engagementMedium\n"
        + "    locale: $locale\n"
        + "  ) {\n"
        + "    template\n"
        + "  }\n"
        + "}";
    final Map<String, Object> variables = new HashMap<>();
    variables.put("user", renderWidgetInput.getUser());
    final WidgetType widgetType = renderWidgetInput.getWidgetType();
    if (widgetType != null) {
      variables.put("widgetType", widgetType.getWidgetType());
    }
    variables.put("engagementMedium", renderWidgetInput.getEngagementMedium());
    variables.put("locale", renderWidgetInput.getLocale());
    return Flowable.fromPublisher(_graphQL(GraphQLInput.newBuilder()
        .setQuery(query)
        .setVariables(variables)
        .build(), renderWidgetInput.getUserJwt(), requestOptions))
        .doOnNext(InternalUtils::throwSquatchExceptionForPotentialGraphQLError)
        .map(graphQLApiResponse -> {
          final GraphQLResult graphQLResult = graphQLApiResponse.getData();
          final String templateString = (String) getNestedMapValue(graphQLResult.getData(),
              "renderWidget", "template");
          return new TextApiResponse(graphQLApiResponse.getHttpResponse(), templateString);
        });
  }

  @Override
  public Publisher<JsonObjectApiResponse> getWidgetConfigValues(
      @Nonnull RenderWidgetInput renderWidgetInput, @Nullable RequestOptions requestOptions) {
    Objects.requireNonNull(renderWidgetInput, "renderWidgetInput");
    final String query = "query renderWidget(\n"
        + "  $user: UserIdInput\n"
        + "  $widgetType: WidgetType\n"
        + "  $engagementMedium: UserEngagementMedium\n"
        + "  $locale: RSLocale\n"
        + ") {\n"
        + "  renderWidget(\n"
        + "    user: $user\n"
        + "    widgetType: $widgetType\n"
        + "    engagementMedium: $engagementMedium\n"
        + "    locale: $locale\n"
        + "  ) {\n"
        + "    widgetConfig {\n"
        + "      values\n"
        + "    }\n"
        + "  }\n"
        + "}";
    final Map<String, Object> variables = new HashMap<>();
    variables.put("user", renderWidgetInput.getUser());
    final WidgetType widgetType = renderWidgetInput.getWidgetType();
    if (widgetType != null) {
      variables.put("widgetType", widgetType.getWidgetType());
    }
    variables.put("engagementMedium", renderWidgetInput.getEngagementMedium());
    variables.put("locale", renderWidgetInput.getLocale());
    return Flowable.fromPublisher(graphQL(GraphQLInput.newBuilder()
        .setQuery(query)
        .setVariables(variables)
        .build(), requestOptions))
        .doOnNext(InternalUtils::throwSquatchExceptionForPotentialGraphQLError)
        .map(graphQLApiResponse -> {
          final GraphQLResult graphQLResult = graphQLApiResponse.getData();
          @SuppressWarnings("unchecked") final Map<String, Object> widgetConfigValues =
              (Map<String, Object>) getNestedMapValue(graphQLResult.getData(), "renderWidget",
                  "widgetConfig", "values");
          return new JsonObjectApiResponse(graphQLApiResponse.getHttpResponse(),
              widgetConfigValues);
        });
  }

  @Override
  public Publisher<JsonObjectApiResponse> userUpsert(@Nonnull UserInput userInput,
      @Nullable RequestOptions requestOptions) {
    return _userUpsert(userInput.getAccountId(), userInput.getId(), userInput, null, null, null,
        requestOptions, false)
        .map(JsonObjectApiResponse::new);
  }

  @Override
  public Publisher<JsonObjectApiResponse> userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable RequestOptions requestOptions) {
    return _userUpsert((String) userInput.get("accountId"), (String) userInput.get("id"), userInput,
        null, null, null, requestOptions, false)
        .map(JsonObjectApiResponse::new);
  }

  @Override
  public Publisher<JsonObjectApiResponse> userUpsertWithUserJwt(@Nonnull String userJwt,
      @Nullable RequestOptions requestOptions) {
    requireNotBlank(userJwt, "userJwt");
    final Map<String, Object> payload = getJwtPayload(userJwt);
    @SuppressWarnings("unchecked") final Map<String, Object> userInput =
        (Map<String, Object>) Objects.requireNonNull(payload.get("user"), "user");
    return _userUpsert((String) userInput.get("accountId"), (String) userInput.get("id"), userInput,
        userJwt, null, null, requestOptions, false)
        .map(JsonObjectApiResponse::new);
  }

  @Override
  public Publisher<JsonObjectApiResponse> widgetUpsert(@Nonnull WidgetUpsertInput widgetUpsertInput,
      @Nullable RequestOptions requestOptions) {
    Objects.requireNonNull(widgetUpsertInput, "widgetUpsertInput");
    return Flowable.fromPublisher(
        _userUpsert(widgetUpsertInput.getAccountId(), widgetUpsertInput.getUserId(),
            widgetUpsertInput.getUserInput(), widgetUpsertInput.getUserJwt(),
            widgetUpsertInput.getWidgetType(), widgetUpsertInput.getEngagementMedium(),
            requestOptions, true))
        .map(JsonObjectApiResponse::new);
  }

  private Flowable<SaaSquatchHttpResponse> _userUpsert(@Nonnull String accountId,
      @Nonnull String userId, @Nonnull Object body, @Nullable String userJwt,
      @Nullable WidgetType widgetType, @Nullable String engagementMedium,
      @Nullable RequestOptions requestOptions, boolean widgetRequest) {
    final URIBuilder uriBuilder = baseUriBuilder(requestOptions);
    final List<String> pathSegments = baseTenantApiPathSegments(requestOptions);
    Collections.addAll(pathSegments, widgetRequest ? "widget" : "open", "account",
        requireNotBlank(accountId, "accountId"), "user", requireNotBlank(userId, "userId"));
    if (widgetRequest) {
      pathSegments.add("upsert");
    }
    mutateUri(uriBuilder, pathSegments, requestOptions);
    if (widgetType != null) {
      uriBuilder.addParameter("widgetType", widgetType.toString());
    }
    if (engagementMedium != null) {
      uriBuilder.addParameter("engagementMedium", engagementMedium);
    }
    final SimpleHttpRequest request = SimpleHttpRequests.put(uriBuilder.toString());
    mutateRequest(request, requestOptions);
    if (userJwt != null) {
      AuthMethod.ofJwt(userJwt).mutateRequest(request);
    }
    setJsonPojoBody(request, body);
    return executeRequest(request);
  }

  @Override
  public Publisher<JsonObjectApiResponse> getUserShareLinks(
      @Nonnull GetUserLinkInput getUserLinkInput, @Nullable RequestOptions requestOptions) {
    final URIBuilder uriBuilder = baseUriBuilder(requestOptions);
    final List<String> pathSegments = baseTenantApiPathSegments(requestOptions);
    Collections.addAll(pathSegments, "account", getUserLinkInput.getAccountId(), "user",
        getUserLinkInput.getUserId(), "shareurls");
    mutateUri(uriBuilder, pathSegments, requestOptions);
    if (getUserLinkInput.getShareMedium() != null) {
      uriBuilder.addParameter("shareMedium", getUserLinkInput.getShareMedium());
    }
    if (getUserLinkInput.getEngagementMedium() != null) {
      uriBuilder.addParameter("engagementMedium", getUserLinkInput.getEngagementMedium());
    }
    final SimpleHttpRequest request = SimpleHttpRequests.get(uriBuilder.toString());
    mutateRequest(request, requestOptions);
    return executeRequest(request).map(JsonObjectApiResponse::new);
  }

  @Override
  public Publisher<JsonObjectApiResponse> logUserEvent(@Nonnull UserEventInput userEventInput,
      @Nullable RequestOptions requestOptions) {
    return _logUserEvent(userEventInput.getAccountId(), userEventInput.getUserId(), userEventInput,
        requestOptions);
  }

  @Override
  public Publisher<JsonObjectApiResponse> logUserEvent(@Nonnull Map<String, Object> userEventInput,
      @Nullable RequestOptions requestOptions) {
    final Map<String, Object> body = userEventInput;
    final String accountId = requireNotBlank((String) body.get("accountId"), "accountId");
    final String userId = requireNotBlank((String) body.get("userId"), "userId");
    return _logUserEvent(accountId, userId, body, requestOptions);
  }

  private Publisher<JsonObjectApiResponse> _logUserEvent(@Nonnull String accountId,
      @Nonnull String userId, @Nonnull Object body, @Nullable RequestOptions requestOptions) {
    final URIBuilder uriBuilder = baseUriBuilder(requestOptions);
    final List<String> pathSegments = baseTenantApiPathSegments(requestOptions);
    Collections.addAll(pathSegments, "open", "account", accountId, "user", userId, "events");
    mutateUri(uriBuilder, pathSegments, requestOptions);
    final SimpleHttpRequest request = SimpleHttpRequests.post(uriBuilder.toString());
    mutateRequest(request, requestOptions);
    setJsonPojoBody(request, body);
    return executeRequest(request).map(JsonObjectApiResponse::new);
  }

  @Override
  public Publisher<JsonObjectApiResponse> applyReferralCode(@Nonnull String accountId,
      @Nonnull String userId, @Nonnull String referralCode,
      @Nullable RequestOptions requestOptions) {
    final URIBuilder uriBuilder = baseUriBuilder(requestOptions);
    final List<String> pathSegments = baseTenantApiPathSegments(requestOptions);
    Collections.addAll(pathSegments, "open", "code", requireNotBlank(referralCode, "referralCode"),
        "account", requireNotBlank(accountId, "accountId"), "user",
        requireNotBlank(userId, "userId"));
    mutateUri(uriBuilder, pathSegments, requestOptions);
    final SimpleHttpRequest request = SimpleHttpRequests.post(uriBuilder.toString());
    mutateRequest(request, requestOptions);
    setJsonStringBody(request, "{}");
    return executeRequest(request).map(JsonObjectApiResponse::new);
  }

  /**
   * All the common url mutations happen here
   */
  private void mutateUri(@Nonnull URIBuilder uriBuilder, @Nonnull List<String> pathSegments,
      @Nullable RequestOptions requestOptions) {
    uriBuilder.setPathSegments(pathSegments);
    if (requestOptions != null) {
      requestOptions.mutateUrl(uriBuilder);
    }
  }

  /**
   * All the common request mutations happen here
   */
  private void mutateRequest(@Nonnull SimpleHttpRequest request,
      @Nullable RequestOptions requestOptions) {
    if (requestOptions != null) {
      requestOptions.mutateRequest(request);
    }
    getAuthMethod(requestOptions).mutateRequest(request);
    final int requestTimeoutMillis = defaultIfNull(
        requestOptions == null ? null : requestOptions.getRequestTimeoutMillis(),
        clientOptions.getRequestTimeoutMillis());
    final int connectTimeoutMillis = defaultIfNull(
        requestOptions == null ? null : requestOptions.getConnectTimeoutMillis(),
        clientOptions.getConnectTimeoutMillis());
    final boolean contentCompressionEnabled = defaultIfNull(
        requestOptions == null ? null : requestOptions.getContentCompressionEnabled(),
        clientOptions.isContentCompressionEnabled());
    request.setConfig(RequestConfig.custom()
        .setResponseTimeout(requestTimeoutMillis, TimeUnit.MILLISECONDS)
        .setConnectTimeout(connectTimeoutMillis, TimeUnit.MILLISECONDS)
        .build());
    if (contentCompressionEnabled) {
      request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
    }
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
   * Get the base url builder with protocol and app domain
   */
  private URIBuilder baseUriBuilder(@Nullable RequestOptions requestOptions) {
    return new URIBuilder().setScheme(scheme).setHost(clientOptions.getAppDomain());
  }

  /**
   * Get the base /api/v1/tenantAlias url path segments
   *
   * @return a mutable list of path segments
   */
  private List<String> baseTenantApiPathSegments(@Nullable RequestOptions requestOptions) {
    final List<String> pathSegments = new ArrayList<>();
    Collections.addAll(pathSegments, "api", "v1", getTenantAlias(requestOptions));
    return pathSegments;
  }

  /**
   * Get the base /a/tenantAlias url path segments
   *
   * @return a mutable list of path segments
   */
  private List<String> baseTenantAPathSegments(@Nullable RequestOptions requestOptions) {
    final List<String> pathSegments = new ArrayList<>();
    Collections.addAll(pathSegments, "a", getTenantAlias(requestOptions));
    return pathSegments;
  }

  private void setJsonPojoBody(@Nonnull SimpleHttpRequest request, Object body) {
    setJsonStringBody(request, gson.toJson(body));
  }

  private void setJsonStringBody(@Nonnull SimpleHttpRequest request, String jsonStr) {
    request.setBody(jsonStr, ContentType.APPLICATION_JSON);
  }

  private Flowable<SaaSquatchHttpResponse> executeRequest(@Nonnull SimpleHttpRequest request) {
    return InternalUtils.executeRequest(httpAsyncClient, request)
        .onErrorResumeNext(t -> Flowable.error(new SaaSquatchIOException(t.getMessage(), t)))
        .<SaaSquatchHttpResponse>map(Client5SaaSquatchHttpResponse::new)
        .doOnNext(this::httpResponseToPossibleException);
  }

  private void httpResponseToPossibleException(@Nonnull SaaSquatchHttpResponse httpResponse) {
    if (httpResponse.getStatusCode() < 300) {
      return;
    }
    final String bodyText = httpResponse.getBodyText();
    final ApiError apiError = ApiError.fromBodyText(bodyText);
    if (apiError != null) {
      throw new SaaSquatchApiException(apiError, httpResponse);
    }
    throw new SaaSquatchUnhandledApiException(httpResponse);
  }

}
