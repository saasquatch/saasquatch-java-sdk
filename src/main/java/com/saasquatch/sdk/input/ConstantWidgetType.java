package com.saasquatch.sdk.input;

import javax.annotation.Nonnull;

final class ConstantWidgetType implements WidgetType {

  private final String widgetType;

  ConstantWidgetType(@Nonnull String widgetType) {
    this.widgetType = widgetType;
  }

  @Override
  @Nonnull
  public String getWidgetType() {
    return this.widgetType;
  }

}
