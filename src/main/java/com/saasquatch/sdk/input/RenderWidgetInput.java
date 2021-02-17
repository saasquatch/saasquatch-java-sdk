package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.getUserIdInputFromUserJwt;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Internal;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Input for rendering a widget.
 *
 * @author sli
 * @see #newBuilder()
 */
public final class RenderWidgetInput {

  private final UserIdInput user;
  private final String userJwt;
  private final WidgetType widgetType;
  private final String engagementMedium;
  private final String locale;

  private RenderWidgetInput(UserIdInput user, String userJwt, WidgetType widgetType,
      String engagementMedium, String locale) {
    this.user = user;
    this.userJwt = userJwt;
    this.widgetType = widgetType;
    this.engagementMedium = engagementMedium;
    this.locale = locale;
  }

  @Nullable
  public UserIdInput getUser() {
    return user;
  }

  @Nullable
  @Internal
  public String getUserJwt() {
    return userJwt;
  }

  @Nullable
  public WidgetType getWidgetType() {
    return widgetType;
  }

  @Nullable
  public String getEngagementMedium() {
    return engagementMedium;
  }

  @Nullable
  public String getLocale() {
    return locale;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private UserIdInput user;
    private String userJwt;
    private WidgetType widgetType;
    private String engagementMedium;
    private String locale;

    private Builder() {}

    /**
     * Set the user IDs with a {@link UserIdInput}
     */
    public Builder setUser(@Nonnull UserIdInput user) {
      this.user = Objects.requireNonNull(user, "user");
      return this;
    }

    /**
     * Convenience method for {@link #setUser(UserIdInput)} that accepts a user JWT
     */
    public Builder setUserWithUserJwt(@Nonnull String userJwt) {
      requireNotBlank(userJwt, "userJwt");
      this.user = getUserIdInputFromUserJwt(userJwt);
      this.userJwt = userJwt;
      return this;
    }

    /**
     * Set the {@link WidgetType} to be rendered
     */
    public Builder setWidgetType(@Nonnull WidgetType widgetType) {
      this.widgetType = Objects.requireNonNull(widgetType, "widgetType");
      return this;
    }

    public Builder setEngagementMedium(@Nonnull String engagementMedium) {
      this.engagementMedium = requireNotBlank(engagementMedium, "engagementMedium");
      return this;
    }

    public Builder setLocale(@Nonnull String locale) {
      this.locale = requireNotBlank(locale, "locale");
      return this;
    }

    public RenderWidgetInput build() {
      return new RenderWidgetInput(user, userJwt, widgetType, engagementMedium, locale);
    }

  }

}
