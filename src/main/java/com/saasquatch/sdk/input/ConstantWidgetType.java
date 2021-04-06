package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.urlDecode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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

  @Nullable
  @Override
  public String getProgramId() {
    if (!widgetType.startsWith("p/")) {
      return null;
    }
    final int indexOfSecondSlash = widgetType.indexOf('/', 2);
    if (indexOfSecondSlash < 0) {
      return null;
    }
    return urlDecode(widgetType.substring(2, indexOfSecondSlash));
  }

}
