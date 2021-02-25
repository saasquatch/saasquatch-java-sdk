package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Beta;
import javax.annotation.Nonnull;

@Beta
public final class ApplyReferralCodeInput {

  private final String accountId;
  private final String userId;
  private final String referralCode;

  private ApplyReferralCodeInput(String accountId, String userId, String referralCode) {
    this.accountId = accountId;
    this.userId = userId;
    this.referralCode = referralCode;
  }

  @Nonnull
  public String getAccountId() {
    return accountId;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  @Nonnull
  public String getReferralCode() {
    return referralCode;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private String userId;
    private String referralCode;

    private Builder() {}

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
      return this;
    }

    public Builder setUserId(@Nonnull String userId) {
      this.userId = requireNotBlank(userId, "userId");
      return this;
    }

    public Builder setReferralCode(@Nonnull String referralCode) {
      this.referralCode = requireNotBlank(referralCode, "referralCode");
      return this;
    }

    public ApplyReferralCodeInput build() {
      return new ApplyReferralCodeInput(requireNotBlank(accountId, "accountId"),
          requireNotBlank(userId, "userId"), requireNotBlank(referralCode, "referralCode"));
    }

  }

}
