package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.urlEncode;

final class ProgramWidgetType implements WidgetType {

  // Lazy init
  private String widgetType;
  private final String programId;
  private final String widgetKey;

  ProgramWidgetType(String programId, String widgetKey) {
    this.programId = programId;
    this.widgetKey = widgetKey;
  }

  @Override
  public String getWidgetType() {
    String t = widgetType;
    if (t == null) {
      widgetType = t = "p/" + urlEncode(programId) + "/w/" + urlEncode(widgetKey);
    }
    return t;
  }

  @Override
  public void blockExternalImpl(ExternalImplBlocker externalImplBlocker) {}

}
