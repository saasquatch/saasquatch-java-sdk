package com.saasquatch.sdk.input;

import javax.annotation.Nonnull;

enum ClassicWidgetType implements WidgetType {

  REFERRER_WIDGET, CONVERSION_WIDGET,;

  @Nonnull
  @Override
  public String getWidgetType() {
    return name();
  }

  @Override
  public void blockExternalImpl(ExternalImplBlocker externalImplBlocker) {}

}
