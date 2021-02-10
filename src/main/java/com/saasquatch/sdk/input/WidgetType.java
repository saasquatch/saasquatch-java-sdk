package com.saasquatch.sdk.input;

import com.saasquatch.sdk.RequestOptions;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.SaaSquatchClient;
import com.saasquatch.sdk.annotations.NoExternalImpl;

/**
 * A SaaSquatch widget type
 *
 * @author sli
 * @see SaaSquatchClient#renderWidget(RenderWidgetInput, RequestOptions)
 * @see SaaSquatchClient#widgetUpsert(UserInput, WidgetType, RequestOptions)
 * @see WidgetTypes
 */
@NoExternalImpl
public interface WidgetType {

  @Nonnull
  String getWidgetType();

}
