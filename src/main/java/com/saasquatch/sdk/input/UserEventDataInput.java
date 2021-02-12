package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableMap;

import com.saasquatch.sdk.internal.json.GsonSerializeNull;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Input for a single instance of user event data
 *
 * @author sli
 * @see #newBuilder()
 * @see UserEventInput.Builder#addEvents(UserEventDataInput...)
 */
public final class UserEventDataInput {

  private final String key;
  private final Date dateTriggered;
  @GsonSerializeNull
  private final Map<String, Object> fields;

  private UserEventDataInput(String key, Date dateTriggered, Map<String, Object> fields) {
    this.key = key;
    this.dateTriggered = dateTriggered;
    this.fields = fields;
  }

  public String getKey() {
    return key;
  }

  public Date getDateTriggered() {
    return dateTriggered;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String key;
    private Date dateTriggered;
    private Map<String, Object> fields;

    private Builder() {}

    public Builder setKey(@Nonnull String key) {
      this.key = requireNotBlank(key, "key");
      return this;
    }

    public Builder setDateTriggered(@Nonnull Date dateTriggered) {
      this.dateTriggered = Objects.requireNonNull(dateTriggered, "dateTriggered");
      return this;
    }

    public Builder setFields(@Nonnull Map<String, Object> fields) {
      this.fields = Objects.requireNonNull(fields, "fields");
      return this;
    }

    public UserEventDataInput build() {
      return new UserEventDataInput(requireNotBlank(key, "key"), dateTriggered,
          fields == null ? null : unmodifiableMap(fields));
    }

  }

}
