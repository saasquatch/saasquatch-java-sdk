package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.getUserIdInputFromUserJwt;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.json.GsonIgnore;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

@Beta
public final class UserAnalyticsEventInput {

  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final String accountId;
  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final String id;
  @GsonIgnore
  private final String userJwt;
  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final String programId;
  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final String type;
  @SuppressWarnings({"unused", "FieldCanBeLocal"})
  private final Map<String, Object> meta;

  private UserAnalyticsEventInput(String accountId, String id, String userJwt, String programId,
      String type, Map<String, Object> meta) {
    this.accountId = accountId;
    this.id = id;
    this.userJwt = userJwt;
    this.programId = programId;
    this.type = type;
    this.meta = meta;
  }

  @Internal
  public String getUserJwt() {
    return userJwt;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private UserIdInput user;
    private String userJwt;
    private String programId;
    private String type;
    private Map<String, Object> meta;

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

    public Builder setProgramId(@Nonnull String programId) {
      this.programId = requireNotBlank(programId, "programId");
      return this;
    }

    public Builder setType(@Nonnull String type) {
      this.type = requireNotBlank(type, "type");
      return this;
    }

    public Builder setMeta(@Nonnull Map<String, Object> meta) {
      this.meta = Objects.requireNonNull(meta, "meta");
      return this;
    }

    public UserAnalyticsEventInput build() {
      return new UserAnalyticsEventInput(user.getAccountId(), user.getId(), userJwt, programId,
          type, meta);
    }

  }

}
