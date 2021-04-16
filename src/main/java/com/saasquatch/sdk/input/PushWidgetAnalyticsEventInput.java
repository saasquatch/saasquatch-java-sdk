package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class PushWidgetAnalyticsEventInput {

  private final UserIdInput user;
  private final String programId;
  private final String engagementMedium;
  private final String shareMedium;

  private PushWidgetAnalyticsEventInput(UserIdInput user, String programId, String engagementMedium,
      String shareMedium) {
    this.user = user;
    this.programId = programId;
    this.engagementMedium = engagementMedium;
    this.shareMedium = shareMedium;
  }

  public UserIdInput getUser() {
    return user;
  }

  public String getProgramId() {
    return programId;
  }

  public String getEngagementMedium() {
    return engagementMedium;
  }

  public String getShareMedium() {
    return shareMedium;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private UserIdInput user;
    private String programId;
    private String engagementMedium;
    private String shareMedium;

    private Builder() {}

    /**
     * Set the user IDs with a {@link UserIdInput}
     */
    public Builder setUser(@Nonnull UserIdInput user) {
      this.user = Objects.requireNonNull(user, "user");
      return this;
    }

    public Builder setProgramId(@Nonnull String programId) {
      this.programId = requireNotBlank(programId, "programId");
      return this;
    }

    public Builder setEngagementMedium(@Nonnull String engagementMedium) {
      this.engagementMedium = requireNotBlank(engagementMedium, "engagementMedium");
      return this;
    }

    public Builder setShareMedium(@Nonnull String shareMedium) {
      this.shareMedium = requireNotBlank(shareMedium, "shareMedium");
      return this;
    }

    public PushWidgetAnalyticsEventInput build() {
      return new PushWidgetAnalyticsEventInput(user, programId, engagementMedium, shareMedium);
    }

  }

}
