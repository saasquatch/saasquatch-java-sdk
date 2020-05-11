package com.saasquatch.sdk.input;

final class ConstantWidgetType implements WidgetType {

  private final String widgetType;

  ConstantWidgetType(String widgetType) {
    this.widgetType = widgetType;
  }

  @Override
  public String getWidgetType() {
    return this.widgetType;
  }

  @Override
  public void blockExternalImpl(ExternalImplBlocker externalImplBlocker) {}

}
