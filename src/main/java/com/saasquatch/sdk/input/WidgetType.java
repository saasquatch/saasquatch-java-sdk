package com.saasquatch.sdk.input;

import com.saasquatch.sdk.SaaSquatchClient;
import com.saasquatch.sdk.annotations.NoExternalImpl;

/**
 * A SaaSquatch widget type
 *
 * @author sli
 * @see SaaSquatchClient#renderWidget(String, String, WidgetType, com.saasquatch.sdk.RequestOptions)
 * @see SaaSquatchClient#widgetUpsert(UserInput, WidgetType, com.saasquatch.sdk.RequestOptions)
 * @see WidgetTypes
 */
@NoExternalImpl
public interface WidgetType {

  String getWidgetType();

  void blockExternalImpl(ExternalImplBlocker externalImplBlocker);

}
