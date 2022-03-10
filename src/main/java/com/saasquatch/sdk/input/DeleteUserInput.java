package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Beta;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Input for deleting a user
 *
 * @author sli
 * @see #newBuilder()
 */
public final class DeleteUserInput {

  private final String accountId;
  private final String userId;
  private final Boolean doNotTrack;
  private final Boolean preserveEmptyAccount;

  private DeleteUserInput(String accountId, String userId, Boolean doNotTrack,
      Boolean preserveEmptyAccount) {
    this.accountId = accountId;
    this.userId = userId;
    this.doNotTrack = doNotTrack;
    this.preserveEmptyAccount = preserveEmptyAccount;
  }

  @Nonnull
  public String getAccountId() {
    return accountId;
  }

  @Nonnull
  public String getUserId() {
    return userId;
  }

  /**
   * @deprecated use {@link #getDoNotTrack()} instead
   */
  @Beta
  @Deprecated
  public boolean isDoNotTrack() {
    return doNotTrack != null && doNotTrack;
  }

  @Beta
  @Nullable
  public Boolean getDoNotTrack() {
    return doNotTrack;
  }

  @Beta
  @Nullable
  public Boolean getPreserveEmptyAccount() {
    return preserveEmptyAccount;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private String userId;
    private Boolean doNotTrack;
    private Boolean preserveEmptyAccount;

    private Builder() {}

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
      return this;
    }

    public Builder setUserId(@Nonnull String userId) {
      this.userId = requireNotBlank(userId, "userId");
      return this;
    }

    public Builder setDoNotTrack(boolean doNotTrack) {
      this.doNotTrack = doNotTrack;
      return this;
    }

    public Builder setPreserveEmptyAccount(boolean preserveEmptyAccount) {
      this.preserveEmptyAccount = preserveEmptyAccount;
      return this;
    }

    public DeleteUserInput build() {
      return new DeleteUserInput(requireNotBlank(accountId, "accountId"),
          requireNotBlank(userId, "userId"), doNotTrack, preserveEmptyAccount);
    }

  }

}
