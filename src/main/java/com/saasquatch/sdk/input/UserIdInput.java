package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import javax.annotation.Nonnull;

/**
 * Input for user IDs
 *
 * @author sli
 * @see #of(String, String)
 */
public final class UserIdInput {

  private final String accountId;
  private final String id;

  private UserIdInput(String accountId, String id) {
    this.accountId = accountId;
    this.id = id;
  }

  @Nonnull
  public String getAccountId() {
    return accountId;
  }

  @Nonnull
  public String getId() {
    return id;
  }

  @Nonnull
  public static UserIdInput of(@Nonnull String accountId, @Nonnull String id) {
    return new UserIdInput(requireNotBlank(accountId, "accountId"), requireNotBlank(id, "id"));
  }

}
