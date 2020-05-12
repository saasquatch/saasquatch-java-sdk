package com.saasquatch.sdk.input;

import javax.annotation.Nonnull;

final class ConstantWidgetType implements WidgetType {

  private final String widgetType;

  ConstantWidgetType(@Nonnull String widgetType) {
    this.widgetType = widgetType;
  }

  @Override
  public String getWidgetType() {
    return this.widgetType;
  }

  @Override
  public void blockExternalImpl(ExternalImplBlocker externalImplBlocker) {}

}
