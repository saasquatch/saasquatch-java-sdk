package com.saasquatch.sdk.input;

enum ClassicWidgetType implements WidgetType {

  REFERRER_WIDGET, CONVERSION_WIDGET,;

  @Override
  public String getWidgetType() {
    return name();
  }

  @Override
  public void blockExternalImpl(ExternalImplBlocker externalImplBlocker) {}

}
