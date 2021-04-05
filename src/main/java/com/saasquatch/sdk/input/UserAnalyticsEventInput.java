package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.getUserIdInputFromUserJwt;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.json.GsonIgnore;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class UserAnalyticsEventInput {

  private final String id;
  private final String accountId;
  @GsonIgnore
  private final String userJwt;
  private final String programId;
  private final String type;
  private final Map<String, Object> meta;

  private UserAnalyticsEventInput(String id, String accountId, String userJwt, String programId,
      String type, Map<String, Object> meta) {
    this.id = id;
    this.accountId = accountId;
    this.userJwt = userJwt;
    this.programId = programId;
    this.type = type;
    this.meta = meta;
  }

  public String getId() {
    return id;
  }

  public String getAccountId() {
    return accountId;
  }

  @Internal
  public String getUserJwt() {
    return userJwt;
  }

  public String getProgramId() {
    return programId;
  }

  public String getType() {
    return type;
  }

  public Map<String, Object> getMeta() {
    return meta;
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

    public Builder setUserJwt(@Nonnull String userJwt) {
      this.userJwt = requireNotBlank(userJwt, "userJwt");
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
      return new UserAnalyticsEventInput(user.getId(), user.getAccountId(), userJwt, programId,
          type, meta);
    }

  }

}
