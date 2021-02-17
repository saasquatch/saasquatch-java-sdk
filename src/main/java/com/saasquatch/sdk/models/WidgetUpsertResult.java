package com.saasquatch.sdk.models;

import java.util.Map;
import com.saasquatch.sdk.annotations.ClassicOnly;

public final class WidgetUpsertResult {

  private final String template;
  private final Map<String, Object> jsOptions;
  private final User user;

  private WidgetUpsertResult(String template, Map<String, Object> jsOptions, User user) {
    this.template = template;
    this.jsOptions = jsOptions;
    this.user = user;
  }

  public String getTemplate() {
    return template;
  }

  @ClassicOnly
  public Map<String, Object> getJsOptions() {
    return jsOptions;
  }

  public User getUser() {
    return user;
  }

}
