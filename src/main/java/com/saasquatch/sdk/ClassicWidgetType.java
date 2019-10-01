package com.saasquatch.sdk;

import com.saasquatch.sdk.annotations.ClassicOnly;

/**
 * Widget type for the classic program
 *
 * @author sli
 */
@ClassicOnly
public enum ClassicWidgetType implements WidgetType {
  REFERRER_WIDGET, CONVERSION_WIDGET,;
}
