package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class UserEventInput {

  private final String accountId;
  private final String userId;
  private final List<UserEventDataInput> events;

  private UserEventInput(String accountId, String userId, List<UserEventDataInput> events) {
    this.accountId = accountId;
    this.userId = userId;
    this.events = events;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getUserId() {
    return userId;
  }

  public List<UserEventDataInput> getEvents() {
    return events;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private String userId;
    private final List<UserEventDataInput> events = new ArrayList<>();

    private Builder() {}

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
      return this;
    }

    public Builder setUserId(@Nonnull String userId) {
      this.userId = requireNotBlank(userId, "userId");
      return this;
    }

    public Builder addEvents(@Nonnull UserEventDataInput... events) {
      for (UserEventDataInput event : events) {
        this.events.add(Objects.requireNonNull(event, "event"));
      }
      return this;
    }

    public UserEventInput build() {
      if (events.isEmpty()) {
        throw new IllegalArgumentException("Empty events");
      }
      return new UserEventInput(requireNotBlank(accountId, "accountId"),
          requireNotBlank(userId, "userId"), unmodifiableList(events));
    }

  }

}
