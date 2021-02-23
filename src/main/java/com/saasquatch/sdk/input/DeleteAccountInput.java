package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import javax.annotation.Nonnull;

/**
 * Input for deleting an account
 *
 * @author sli
 * @see #newBuilder()
 */
public final class DeleteAccountInput {

  private final String accountId;
  private final boolean doNotTrack;

  private DeleteAccountInput(String accountId, boolean doNotTrack) {
    this.accountId = accountId;
    this.doNotTrack = doNotTrack;
  }

  public String getAccountId() {
    return accountId;
  }

  public boolean isDoNotTrack() {
    return doNotTrack;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private boolean doNotTrack = false;

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
