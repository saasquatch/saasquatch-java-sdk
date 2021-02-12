package com.saasquatch.sdk.input;

import com.saasquatch.sdk.annotations.Internal;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class WidgetUpsertInput {

  private final Object userInput;
  private final String accountId;
  private final String userId;
  private final WidgetType widgetType;

  private WidgetUpsertInput(Object userInput, String accountId, String userId,
      WidgetType widgetType) {
    this.userInput = userInput;
    this.accountId = accountId;
    this.userId = userId;
    this.widgetType = widgetType;
  }

  public Object getUserInput() {
    return userInput;
  }

  @Internal
  public String getAccountId() {
    return accountId;
  }

  @Internal
  public String getUserId() {
    return userId;
  }

  public WidgetType getWidgetType() {
    return widgetType;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private Object userInput;
    private String accountId;
    private String userId;
    private WidgetType widgetType;

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

    public Builder setWidgetType(WidgetType widgetType) {
      this.widgetType = Objects.requireNonNull(widgetType, "widgetType");
      return this;
    }

    public WidgetUpsertInput build() {
      return new WidgetUpsertInput(userInput, accountId, userId, widgetType);
    }

  }

}
