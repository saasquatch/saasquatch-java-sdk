package com.saasquatch.sdk.input;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

enum ClassicWidgetType implements WidgetType {

  REFERRER_WIDGET, CONVERSION_WIDGET,;

  @Nonnull
  @Override
  public String getWidgetType() {
    return name();
  }

  @Nullable
  @Override
  public String getProgramId() {
    return null;
  }

}
