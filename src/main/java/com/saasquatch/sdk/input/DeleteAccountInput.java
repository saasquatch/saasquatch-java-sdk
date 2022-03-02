package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Beta;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Input for deleting an account
 *
 * @author sli
 * @see #newBuilder()
 */
public final class DeleteAccountInput {

  private final String accountId;
  private final Boolean doNotTrack;

  private DeleteAccountInput(String accountId, Boolean doNotTrack) {
    this.accountId = accountId;
    this.doNotTrack = doNotTrack;
  }

  @Nonnull
  public String getAccountId() {
    return accountId;
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

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private Boolean doNotTrack;

    private Builder() {}

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
      return this;
    }

    public Builder setDoNotTrack(boolean doNotTrack) {
      this.doNotTrack = doNotTrack;
      return this;
    }

    public DeleteAccountInput build() {
      return new DeleteAccountInput(requireNotBlank(accountId, "accountId"), doNotTrack);
    }

  }

}
