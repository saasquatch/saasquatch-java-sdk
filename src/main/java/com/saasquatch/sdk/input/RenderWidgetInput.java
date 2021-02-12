package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

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
  private final WidgetType widgetType;
  private final String engagementMedium;
  private final String locale;

  private RenderWidgetInput(UserIdInput user, WidgetType widgetType, String engagementMedium,
      String locale) {
    this.user = user;
    this.widgetType = widgetType;
    this.engagementMedium = engagementMedium;
    this.locale = locale;
  }

  @Nullable
  public UserIdInput getUser() {
    return user;
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
    private WidgetType widgetType;
    private String engagementMedium;
    private String locale;

    private Builder() {}

    public Builder setUser(@Nonnull UserIdInput user) {
      this.user = Objects.requireNonNull(user, "user");
      return this;
    }

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
      return new RenderWidgetInput(user, widgetType, engagementMedium, locale);
    }

  }

}
