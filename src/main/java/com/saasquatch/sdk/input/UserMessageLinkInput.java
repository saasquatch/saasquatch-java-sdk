package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.SaaSquatchClient;

/**
 * Input for creating a user message link.
 *
 * @author sli
 * @see #newBuilder()
 * @see SaaSquatchClient#buildUserMessageLink(UserMessageLinkInput,
 *      com.saasquatch.sdk.RequestOptions)
 */
public final class UserMessageLinkInput {

  private final String accountId;
  private final String userId;
  private final String programId;
  private final String shareMedium;
  private final String engagementMedium;

  UserMessageLinkInput(String accountId, String userId, String programId, String shareMedium,
      String engagementMedium) {
    this.accountId = accountId;
    this.userId = userId;
    this.programId = programId;
    this.shareMedium = shareMedium;
    this.engagementMedium = engagementMedium;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getUserId() {
    return userId;
  }

  public String getProgramId() {
    return programId;
  }

  public String getShareMedium() {
    return shareMedium;
  }

  public String getEngagementMedium() {
    return engagementMedium;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private String userId;
    private String programId;
    private String shareMedium;
    private String engagementMedium;

    public Builder() {}

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
      return this;
    }

    public Builder setUserId(@Nonnull String userId) {
      this.userId = requireNotBlank(userId, "userId");
      return this;
    }

    public Builder setProgramId(@Nonnull String programId) {
      this.programId = requireNotBlank(programId, "programId");
      return this;
    }

    public Builder setShareMedium(@Nonnull String shareMedium) {
      this.shareMedium = requireNotBlank(shareMedium, "shareMedium");
      return this;
    }

    public Builder setEngagementMedium(@Nonnull String engagementMedium) {
      this.engagementMedium = requireNotBlank(engagementMedium, "engagementMedium");
      return this;
    }

    public UserMessageLinkInput build() {
      return new UserMessageLinkInput(requireNotBlank(accountId, "accountId"),
          requireNotBlank(userId, "userId"), programId, requireNotBlank(shareMedium, "shareMedium"),
          engagementMedium);
    }

  }

}
