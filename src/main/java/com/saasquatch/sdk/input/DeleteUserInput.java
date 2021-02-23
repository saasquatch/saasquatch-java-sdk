package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Beta;
import javax.annotation.Nonnull;

/**
 * Input for deleting a user
 *
 * @author sli
 * @see #newBuilder()
 */
public final class DeleteUserInput {

  private final String accountId;
  private final String userId;
  private final boolean doNotTrack;

  private DeleteUserInput(String accountId, String userId, boolean doNotTrack) {
    this.accountId = accountId;
    this.userId = userId;
    this.doNotTrack = doNotTrack;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getUserId() {
    return userId;
  }

  @Beta
  public boolean isDoNotTrack() {
    return doNotTrack;
  }
  
  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private String userId;
    private boolean doNotTrack = false;

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

    public DeleteUserInput build() {
      return new DeleteUserInput(requireNotBlank(accountId, "accountId"),
          requireNotBlank(userId, "userId"), doNotTrack);
    }

  }

}
