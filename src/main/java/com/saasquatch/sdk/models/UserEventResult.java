package com.saasquatch.sdk.models;

import java.util.List;

public class UserEventResult implements Model {

  private final String accountId;
  private final String userId;
  private final List<UserEventData> events;

  public UserEventResult(String accountId, String userId, List<UserEventData> events) {
    this.accountId = accountId;
    this.userId = userId;
    this.events = events;
  }

  public String getAccountId() {
    return accountId;
  }

  public String getUserId() {
    return userId;
  }

  public List<UserEventData> getEvents() {
    return events;
  }

}
