package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.annotations.ClassicOnly;

/**
 * Utilities for creating {@link WidgetType}s
 *
 * @author sli
 */
public final class WidgetTypes {

  private WidgetTypes() {}

  public static WidgetType ofProgramWidget(@Nonnull String programId,
      @Nonnull String widgetKey) {
    return new ProgramWidgetType(requireNotBlank(programId, "programId"),
        requireNotBlank(widgetKey, "widgetKey"));
  }

  @ClassicOnly
  public static WidgetType classicReferrerWidget() {
    return ClassicWidgetType.REFERRER_WIDGET;
  }

  @ClassicOnly
  public static WidgetType classicConversionWidget() {
    return ClassicWidgetType.CONVERSION_WIDGET;
  }

  @Beta
  public static WidgetType ofConstant(@Nonnull String widgetType) {
    return new ConstantWidgetType(requireNotBlank(widgetType, "widgetType"));
  }

}
