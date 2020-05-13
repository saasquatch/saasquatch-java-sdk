package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.addAllRejectingNull;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.SaaSquatchClient;

/**
 * Input for log user event API
 *
 * @author sli
 * @see #newBuilder()
 * @see SaaSquatchClient#logUserEvent(UserEventInput, com.saasquatch.sdk.RequestOptions)
 */
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
    private List<UserEventDataInput> events;

    private Builder() {}

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
      return this;
    }

    public Builder setUserId(@Nonnull String userId) {
      this.userId = requireNotBlank(userId, "userId");
      return this;
    }

    public Builder setEvents(@Nonnull List<UserEventDataInput> events) {
      // Ensure mutability
      if (events instanceof ArrayList || events instanceof LinkedList) {
        this.events = events;
      } else {
        this.events = new ArrayList<>(events);
      }
      return this;
    }

    public Builder addEvents(@Nonnull UserEventDataInput... events) {
      if (this.events == null) {
        this.events = new ArrayList<>();
      }
      addAllRejectingNull("events", this.events, events);
      return this;
    }

    public UserEventInput build() {
      if (events == null || events.isEmpty()) {
        throw new IllegalArgumentException("Empty events");
      }
      return new UserEventInput(requireNotBlank(accountId, "accountId"),
          requireNotBlank(userId, "userId"), unmodifiableList(events));
    }

  }

}
