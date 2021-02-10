package com.saasquatch.sdk.models;

import java.util.Date;
import java.util.Map;

public final class UserEventData {

  private final String id;
  private final String key;
  private final Map<String, Object> fields;
  private final Date dateTriggered;
  private final Date dateReceived;
  private final Date dateProcessed;

  private UserEventData(String id, String key, Map<String, Object> fields, Date dateTriggered,
      Date dateReceived, Date dateProcessed) {
    this.id = id;
    this.key = key;
    this.fields = fields;
    this.dateTriggered = dateTriggered;
    this.dateReceived = dateReceived;
    this.dateProcessed = dateProcessed;
  }

  public String getId() {
    return id;
  }

  public String getKey() {
    return key;
  }

  public Map<String, Object> getFields() {
    return fields;
  }

  public Date getDateTriggered() {
    return dateTriggered;
  }

  public Date getDateReceived() {
    return dateReceived;
  }

  public Date getDateProcessed() {
    return dateProcessed;
  }

}
