package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.urlEncode;

import javax.annotation.Nonnull;

class GlobalWidgetType implements WidgetType {

  // Lazy init
  private String widgetType;
  @Nonnull
  private final String globalWidgetKey;

  GlobalWidgetType(@Nonnull String globalWidgetKey) {
    this.globalWidgetKey = globalWidgetKey;
  }

  @Nonnull
  @Override
  public String getWidgetType() {
    String t = widgetType;
    if (t == null) {
      widgetType = t = "w/" + urlEncode(globalWidgetKey);
    }
    return t;
  }

}
