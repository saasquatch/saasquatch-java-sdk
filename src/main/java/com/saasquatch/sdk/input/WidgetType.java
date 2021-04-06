package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.RequestOptions;
import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.annotations.ClassicOnly;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.SaaSquatchClient;
import com.saasquatch.sdk.annotations.NoExternalImpl;
import javax.annotation.Nullable;

/**
 * SaaSquatch widget type
 *
 * @author sli
 * @see SaaSquatchClient#renderWidget(RenderWidgetInput, RequestOptions)
 * @see SaaSquatchClient#widgetUpsert(WidgetUpsertInput, RequestOptions)
 */
@NoExternalImpl
public interface WidgetType {

  @Nonnull
  String getWidgetType();

  @Nullable
  String getProgramId();

  static WidgetType ofProgramWidget(@Nonnull String programId,
      @Nonnull String programWidgetKey) {
    return new ProgramWidgetType(requireNotBlank(programId, "programId"),
        requireNotBlank(programWidgetKey, "programWidgetKey"));
  }

  static WidgetType ofGlobalWidget(@Nonnull String globalWidgetKey) {
    return new GlobalWidgetType(requireNotBlank(globalWidgetKey, "globalWidgetKey"));
  }

  @ClassicOnly
  static WidgetType classicReferrerWidget() {
    return ClassicWidgetType.REFERRER_WIDGET;
  }

  @ClassicOnly
  static WidgetType classicConversionWidget() {
    return ClassicWidgetType.CONVERSION_WIDGET;
  }

  @Beta
  static WidgetType ofConstant(@Nonnull String widgetType) {
    return new ConstantWidgetType(requireNotBlank(widgetType, "widgetType"));
  }

}
