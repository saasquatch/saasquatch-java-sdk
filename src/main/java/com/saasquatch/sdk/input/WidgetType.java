package com.saasquatch.sdk.input;

import com.saasquatch.sdk.annotations.Internal;

/**
 * A SaaSquatch widget type
 *
 * @author sli
 * @see #of(String, String)
 * @see ClassicWidgetType
 */
public interface WidgetType {

  @Internal
  String getWidgetType();

}
