package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.getJwtPayload;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Internal;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class WidgetUpsertInput {

  private final Object userInput;
  private final String accountId;
  private final String userId;
  private final String userJwt;
  private final WidgetType widgetType;
  private final String engagementMedium;

  private WidgetUpsertInput(Object userInput, String accountId, String userId, String userJwt,
      WidgetType widgetType, String engagementMedium) {
    this.userInput = userInput;
    this.accountId = accountId;
    this.userId = userId;
    this.userJwt = userJwt;
    this.widgetType = widgetType;
    this.engagementMedium = engagementMedium;
  }

  @Nonnull
  @Internal
  public Object getUserInput() {
    return userInput;
  }

  @Nonnull
  @Internal
  public String getAccountId() {
    return accountId;
  }

  @Nonnull
  @Internal
  public String getUserId() {
    return userId;
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

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Object userInput;
    private String accountId;
    private String userId;
    private String userJwt;
    private WidgetType widgetType;
    private String engagementMedium;

    private Builder() {}

    public Builder setUserInput(@Nonnull UserInput userInput) {
      this.userInput = Objects.requireNonNull(userInput, "userInput");
      this.accountId = userInput.getAccountId();
      this.userId = userInput.getId();
      return this;
    }

    public Builder setUserInput(@Nonnull Map<String, Object> userInput) {
      this.userInput = Objects.requireNonNull(userInput, "userInput");
      this.accountId = (String) userInput.get("accountId");
      this.userId = (String) userInput.get("id");
      return this;
    }

    public Builder setUserInputWithUserJwt(@Nonnull String userJwt) {
      requireNotBlank(userJwt, "userJwt");
      final Map<String, Object> payload = getJwtPayload(userJwt);
      @SuppressWarnings("unchecked") final Map<String, Object> userInput =
          (Map<String, Object>) payload.get("user");
      setUserInput(userInput);
      this.userJwt = userJwt;
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

    public WidgetUpsertInput build() {
      return new WidgetUpsertInput(userInput, accountId, userId, userJwt, widgetType,
          engagementMedium);
    }

  }

}
