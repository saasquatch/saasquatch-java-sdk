package com.saasquatch.sdk.input;

import com.saasquatch.sdk.annotations.ClassicOnly;

/**
 * Widget type for the classic program
 *
 * @author sli
 */
@ClassicOnly
public enum ClassicWidgetType implements WidgetType {
  REFERRER_WIDGET, CONVERSION_WIDGET,;

  @Override
  public String getWidgetType() {
    return name();
  }

}
