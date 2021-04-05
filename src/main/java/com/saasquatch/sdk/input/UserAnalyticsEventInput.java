package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.internal.InternalUtils;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class UserAnalyticsEventInput {

  private final String id;
  private final String accountId;
  private final String programId;
  private final String type;
  private final Map<String, Object> meta;

  private UserAnalyticsEventInput(String id, String accountId, String programId, String type, Map<String, Object> meta) {
    this.id = id;
    this.accountId = accountId;
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

    private String id;
    private String accountId;
    private String programId;
    private String type;
    private Map<String, Object> meta;

    private Builder() {}

    public Builder setId(@Nonnull String id) {
      this.id = requireNotBlank(id, "id");
      return this;
    }

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
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
      return new UserAnalyticsEventInput(id, accountId, programId, type, meta);
    }

  }

}
