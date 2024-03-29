package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableList;

import com.saasquatch.sdk.SaaSquatchClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
  private final String idempotencyKey;
  private final List<UserEventDataInput> events;

  private UserEventInput(String accountId, String userId, String idempotencyKey,
      List<UserEventDataInput> events) {
    this.accountId = accountId;
    this.userId = userId;
    this.idempotencyKey = idempotencyKey;
    this.events = events;
  }

  @Nonnull
  public String getAccountId() {
    return accountId;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  @Nullable
  public String getIdempotencyKey() {
    return idempotencyKey;
  }

  @Nonnull
  public List<UserEventDataInput> getEvents() {
    return events;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private String userId;
    private String idempotencyKey;
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

    public Builder setIdempotencyKey(String idempotencyKey) {
      this.idempotencyKey = requireNotBlank(idempotencyKey, "idempotencyKey");
      return this;
    }

    public Builder addEvents(@Nonnull UserEventDataInput... events) {
      if (this.events == null) {
        this.events = new ArrayList<>();
      }
      for (UserEventDataInput event : events) {
        this.events.add(Objects.requireNonNull(event, "event"));
      }
      return this;
    }

    public UserEventInput build() {
      if (events == null || events.isEmpty()) {
        throw new IllegalArgumentException("Empty events");
      }
      return new UserEventInput(requireNotBlank(accountId, "accountId"),
          requireNotBlank(userId, "userId"), idempotencyKey, unmodifiableList(events));
    }

  }

}
