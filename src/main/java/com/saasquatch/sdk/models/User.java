package com.saasquatch.sdk.models;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import com.saasquatch.sdk.annotations.ClassicOnly;

public final class User implements Model {

  private final String id;
  private final String accountId;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String emailHash;
  private final String cookieId;
  private final String paymentProviderId;
  private final String referralCode;
  private final Map<String, String> referralCodes;
  private final String locale;
  private final String imageUrl;
  private final Boolean referable;
  private final String firstSeenIP;
  private final String lastSeenIP;
  private final Date dateCreated;
  private final Date dateBlocked;
  private final Set<String> referredByCodes;
  private final Map<String, Object> shareLinks;
  private final Map<String, Map<String, Map<String, String>>> programShareLinks;
  private final Map<String, Object> customFields;
  private final Set<String> segments;

  private User(String id, String accountId, String firstName, String lastName, String email,
      String emailHash, String cookieId, String paymentProviderId, String referralCode,
      Map<String, String> referralCodes, String locale, String imageUrl, Boolean referable,
      String firstSeenIP, String lastSeenIP, Date dateCreated, Date dateBlocked,
      Set<String> referredByCodes, Map<String, Object> shareLinks,
      Map<String, Map<String, Map<String, String>>> programShareLinks,
      Map<String, Object> customFields, Set<String> segments) {
    this.id = id;
    this.accountId = accountId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.emailHash = emailHash;
    this.cookieId = cookieId;
    this.paymentProviderId = paymentProviderId;
    this.referralCode = referralCode;
    this.referralCodes = referralCodes;
    this.locale = locale;
    this.imageUrl = imageUrl;
    this.referable = referable;
    this.firstSeenIP = firstSeenIP;
    this.lastSeenIP = lastSeenIP;
    this.dateCreated = dateCreated;
    this.dateBlocked = dateBlocked;
    this.referredByCodes = referredByCodes;
    this.shareLinks = shareLinks;
    this.programShareLinks = programShareLinks;
    this.customFields = customFields;
    this.segments = segments;
  }

  public String getId() {
    return id;
  }

  public String getAccountId() {
    return accountId;
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

  public String getEmailHash() {
    return emailHash;
  }

  public String getCookieId() {
    return cookieId;
  }

  @ClassicOnly
  public String getPaymentProviderId() {
    return paymentProviderId;
  }

  @ClassicOnly
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

  public String getFirstSeenIP() {
    return firstSeenIP;
  }

  public String getLastSeenIP() {
    return lastSeenIP;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public Date getDateBlocked() {
    return dateBlocked;
  }

  public Set<String> getReferredByCodes() {
    return referredByCodes;
  }

  @ClassicOnly
  public Map<String, Object> getShareLinks() {
    return shareLinks;
  }

  public Map<String, Map<String, Map<String, String>>> getProgramShareLinks() {
    return programShareLinks;
  }

  public Map<String, Object> getCustomFields() {
    return customFields;
  }

  public Set<String> getSegments() {
    return segments;
  }

}
