package com.saasquatch.sdk;

import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.auth.AuthMethod;
import com.saasquatch.sdk.input.GetUserLinkInput;
import com.saasquatch.sdk.input.GraphQLInput;
import com.saasquatch.sdk.input.RenderWidgetInput;
import com.saasquatch.sdk.input.UserEventInput;
import com.saasquatch.sdk.input.UserInput;
import com.saasquatch.sdk.input.WidgetUpsertInput;
import com.saasquatch.sdk.models.User;
import com.saasquatch.sdk.models.UserEventResult;
import com.saasquatch.sdk.models.WidgetUpsertResult;
import com.saasquatch.sdk.output.GraphQLApiResponse;
import com.saasquatch.sdk.output.JsonObjectApiResponse;
import com.saasquatch.sdk.output.TextApiResponse;
import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.reactivestreams.Publisher;

/**
 * Main entry point for SaaSquatch APIs
 *
 * @author sli
 * @see SaaSquatchClient#createForTenant(String)
 */
public interface SaaSquatchClient extends Closeable {

  /**
   * Initialize a {@link SaaSquatchClient} with a tenantAlias and default options.
   *
   * @param tenantAlias Your tenantAlias. This will be the default tenantAlias for all your
   *                    requests. If you are in a multi-tenant environment, you should be using
   *                    {@link SaaSquatchClient#create(ClientOptions)} without a tenantAlias, and
   *                    then pass the tenantAlias you want to use in every request via {@link
   *                    RequestOptions.Builder#setTenantAlias(String)}
   * @see #create(ClientOptions)
   */
  static SaaSquatchClient createForTenant(@Nonnull String tenantAlias) {
    return create(ClientOptions.newBuilder().setTenantAlias(tenantAlias).build());
  }

  /**
   * Initialize a {@link SaaSquatchClient} with a custom {@link ClientOptions}.
   *
   * @see ClientOptions#newBuilder()
   */
  static SaaSquatchClient create(@Nonnull ClientOptions clientOptions) {
    return new SaaSquatchClientImpl(Objects.requireNonNull(clientOptions, "clientOptions"));
  }

  /**
   * Build a message link for a given user with the specified program and mediums.<br> Note that
   * this method simply builds a URL and does not do any I/O. Depending on the combination of
   * shareMedium and engagementMedium, the URL built may not work. And since this method does not
   * make an API call, the configured {@link AuthMethod} and HTTP headers are ignored.<br>
   * <a href="https://docs.saasquatch.com/features/message-links/">Link to official
   * docs</a>
   */
  @Nonnull
  String buildUserMessageLink(@Nonnull GetUserLinkInput getUserLinkInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Execute a GraphQL request
   */
  Publisher<GraphQLApiResponse> graphQL(@Nonnull GraphQLInput graphQLInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Get a user.<br> By default, the result of the response can be unmarshalled to {@link
   * User}.<br>
   * <a href="https://docs.saasquatch.com/api/methods/#open_get_user">Link to official
   * docs</a>
   */
  Publisher<JsonObjectApiResponse> getUser(@Nonnull String accountId, @Nonnull String userId,
      @Nullable RequestOptions requestOptions);

  /**
   * Convenience method for {@link #getUser(String, String, RequestOptions)} that simply accepts a
   * user JWT.
   */
  Publisher<JsonObjectApiResponse> getUserWithUserJwt(@Nonnull String userJwt,
      @Nullable RequestOptions requestOptions);

  /**
   * Render a widget.<br> The response is the widget HTML.
   */
  Publisher<TextApiResponse> renderWidget(
      @Nonnull RenderWidgetInput renderWidgetInput, @Nullable RequestOptions requestOptions);

  /**
   * "Render" a widget by getting its widget config values. Note that this method does not work with
   * classic widgets.
   */
  Publisher<JsonObjectApiResponse> getWidgetConfigValues(
      @Nonnull RenderWidgetInput renderWidgetInput, @Nullable RequestOptions requestOptions);

  /**
   * Create or update a user.<br> By default, the result of the response can be unmarshalled to
   * {@link User}.<br>
   * <a href="https://docs.saasquatch.com/api/methods/#open_user_upsert">Link to official
   * docs</a>
   */
  Publisher<JsonObjectApiResponse> userUpsert(@Nonnull UserInput userInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Create or update a user.<br> By default, the result of the response can be unmarshalled to
   * {@link User}.<br>
   * <a href="https://docs.saasquatch.com/api/methods/#open_user_upsert">Link to official
   * docs</a>
   */
  Publisher<JsonObjectApiResponse> userUpsert(@Nonnull Map<String, Object> userInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Create or update a user.<br> By default, the result of the response can be unmarshalled to
   * {@link User}.<br>
   * <a href="https://docs.saasquatch.com/api/methods/#open_user_upsert">Link to official
   * docs</a>
   */
  Publisher<JsonObjectApiResponse> userUpsertWithUserJwt(@Nonnull String userJwt,
      @Nullable RequestOptions requestOptions);

  /**
   * Create or update a user and render the widget.<br> By default, the result of the response can
   * be unmarshalled to {@link WidgetUpsertResult}.
   */
  Publisher<JsonObjectApiResponse> widgetUpsert(@Nonnull WidgetUpsertInput widgetUpsertInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Get a Map of a user's share links<br>
   * <a href="https://docs.saasquatch.com/api/methods/#lookup-a-users-share-urls">Link to
   * official docs</a>
   */
  Publisher<JsonObjectApiResponse> getUserShareLinks(@Nonnull GetUserLinkInput getUserLinkInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Log a user event.<br> By default, the result of the response can be unmarshalled to {@link
   * UserEventResult}.<br>
   * <a href="https://docs.saasquatch.com/api/methods/#trackEvent">Link to official
   * docs</a>
   */
  Publisher<JsonObjectApiResponse> logUserEvent(@Nonnull UserEventInput userEventInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Log a user event.<br> By default, the result of the response can be unmarshalled to {@link
   * UserEventResult}.<br>
   * <a href="https://docs.saasquatch.com/api/methods/#trackEvent">Link to official
   * docs</a>
   */
  Publisher<JsonObjectApiResponse> logUserEvent(@Nonnull Map<String, Object> userEventInput,
      @Nullable RequestOptions requestOptions);

  /**
   * Apply a referral code.<br>
   * <a href="https://docs.saasquatch.com/api/methods/#open_apply_code">Link to official
   * docs</a>
   */
  @Beta
  Publisher<JsonObjectApiResponse> applyReferralCode(@Nonnull String accountId,
      @Nonnull String userId, @Nonnull String referralCode,
      @Nullable RequestOptions requestOptions);

}
