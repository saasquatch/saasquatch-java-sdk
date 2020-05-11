package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableMap;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.SaaSquatchClient;
import com.saasquatch.sdk.annotations.ClassicOnly;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.internal.json.GsonUtils;
import com.saasquatch.sdk.internal.json.HasCustomJsonSerialization;

/**
 * Input for a user
 *
 * @author sli
 * @see #newBuilder()
 * @see SaaSquatchClient#userUpsert(UserInput, com.saasquatch.sdk.RequestOptions)
 * @see SaaSquatchClient#widgetUpsert(UserInput, WidgetType, com.saasquatch.sdk.RequestOptions)
 */
public final class UserInput implements HasCustomJsonSerialization {

  private final String accountId;
  private final String id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String paymentProviderId;
  private final String referralCode;
  private final Map<String, String> referralCodes;
  private final String locale;
  private final String imageUrl;
  private final Boolean referable;
  private final Set<String> referredByCodes;
  private final Map<String, Object> referredBy;
  private final Map<String, Object> customFields;
  private final Set<String> segments;

  private UserInput(String accountId, String id, String firstName, String lastName, String email,
      String paymentProviderId, String referralCode, Map<String, String> referralCodes,
      String locale, String imageUrl, Boolean referable, Set<String> referredByCodes,
      Map<String, Object> referredBy, Map<String, Object> customFields, Set<String> segments) {
    this.accountId = accountId;
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.paymentProviderId = paymentProviderId;
    this.referralCode = referralCode;
    this.referralCodes = referralCodes;
    this.locale = locale;
    this.imageUrl = imageUrl;
    this.referable = referable;
    this.referredByCodes = referredByCodes;
    this.referredBy = referredBy;
    this.customFields = customFields;
    this.segments = segments;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  @ClassicOnly
  public String getPaymentProviderId() {
    return paymentProviderId;
  }

  public String getReferralCode() {
    return referralCode;
  }

  public Map<String, String> getReferralCodes() {
    return referralCodes;
  }

  public String getLocale() {
    return locale;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public Boolean getReferable() {
    return referable;
  }

  public Set<String> getReferredByCodes() {
    return referredByCodes;
  }

  @ClassicOnly
  public Map<String, Object> getReferredBy() {
    return referredBy;
  }

  public Map<String, Object> getCustomFields() {
    return customFields;
  }

  public Set<String> getSegments() {
    return segments;
  }

  @Internal
  @Override
  public String toJsonString() {
    return GsonUtils.toJsonExcludingNullRootLevalFields(this);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {

    private String accountId;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String paymentProviderId;
    private String referralCode;
    private Map<String, String> referralCodes;
    private String locale;
    private String imageUrl;
    private Boolean referable;
    private Set<String> referredByCodes;
    private Map<String, Object> referredBy;
    private Map<String, Object> customFields;
    private Set<String> segments;

    private Builder() {}

    public Builder setAccountId(@Nonnull String accountId) {
      this.accountId = requireNotBlank(accountId, "accountId");
      return this;
    }

    public Builder setId(@Nonnull String id) {
      this.id = requireNotBlank(id, "id");
      return this;
    }

    public Builder setFirstName(@Nonnull String firstName) {
      this.firstName = Objects.requireNonNull(firstName, "firstName");
      return this;
    }

    public Builder setLastName(@Nonnull String lastName) {
      this.lastName = Objects.requireNonNull(lastName, "lastName");
      return this;
    }

    public Builder setEmail(@Nonnull String email) {
      this.email = requireNotBlank(email, "email");
      return this;
    }

    @ClassicOnly
    public Builder setPaymentProviderId(@Nonnull String paymentProviderId) {
      this.paymentProviderId = requireNotBlank(paymentProviderId, "paymentProviderId");
      return this;
    }

    @ClassicOnly
    public Builder setReferralCode(@Nonnull String referralCode) {
      this.referralCode = requireNotBlank(referralCode, "referralCode");
      return this;
    }

    public Builder setReferralCodes(@Nonnull Map<String, String> referralCodes) {
      this.referralCodes = Objects.requireNonNull(referralCodes, "referralCodes");
      return this;
    }

    public Builder setLocale(@Nonnull String locale) {
      this.locale = requireNotBlank(locale, "locale");
      return this;
    }

    public Builder setLocale(@Nonnull Locale locale) {
      return setLocale(locale.toString());
    }

    public Builder setImageUrl(@Nonnull String imageUrl) {
      this.imageUrl = requireNotBlank(imageUrl, "imageUrl");
      return this;
    }

    public Builder setReferable(boolean referable) {
      this.referable = referable;
      return this;
    }

    public Builder setReferredByCodes(@Nonnull Set<String> referredByCodes) {
      this.referredByCodes = Objects.requireNonNull(referredByCodes, "referredByCodes");
      return this;
    }

    @ClassicOnly
    public Builder setReferredBy(@Nonnull Map<String, Object> referredBy) {
      this.referredBy = Objects.requireNonNull(referredBy, "referredBy");
      return this;
    }

    public Builder setCustomFields(@Nonnull Map<String, Object> customFields) {
      this.customFields = Objects.requireNonNull(customFields, "customFields");
      return this;
    }

    public Builder setSegments(@Nonnull Set<String> segments) {
      // Ensure mutability and ordering
      if (segments instanceof LinkedHashSet || segments instanceof TreeSet) {
        this.segments = segments;
      } else {
        this.segments = new LinkedHashSet<>(segments);
      }
      return this;
    }

    public Builder addToSegments(@Nonnull String... segments) {
      if (this.segments == null) {
        this.segments = new LinkedHashSet<>();
      }
      Collections.addAll(this.segments, segments);
      return this;
    }

    public Builder removeFromSegments(@Nonnull String... segments) {
      if (this.segments == null) {
        this.segments = new LinkedHashSet<>();
      }
      for (String segment : segments) {
        this.segments.add('~' + segment);
      }
      return this;
    }

    public UserInput build() {
      return new UserInput(requireNotBlank(accountId, "accountId"), requireNotBlank(id, "id"),
          firstName, lastName, email, paymentProviderId, referralCode,
          referralCodes == null ? null : unmodifiableMap(referralCodes), locale, imageUrl,
          referable, referredByCodes == null ? null : unmodifiableSet(referredByCodes),
          referredBy == null ? null : unmodifiableMap(referredBy),
          customFields == null ? null : unmodifiableMap(customFields),
          segments == null ? null : unmodifiableSet(segments));
    }

  }

}
