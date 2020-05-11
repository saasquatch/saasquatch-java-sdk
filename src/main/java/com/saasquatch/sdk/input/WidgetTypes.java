package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.annotations.ClassicOnly;

public final class WidgetTypes {

  public static ProgramWidgetType ofProgramWidget(@Nonnull String programId,
      @Nonnull String widgetKey) {
    return new ProgramWidgetType(requireNotBlank(programId, "programId"),
        requireNotBlank(widgetKey, "widgetKey"));
  }

  @ClassicOnly
  public static WidgetType classicReferrer() {
    return ConstantWidgetTypeHolder.REFERRER;
  }

  @ClassicOnly
  public static WidgetType classicConversion() {
    return ConstantWidgetTypeHolder.CONVERSION;
  }

  @Beta
  public static WidgetType ofConstant(String widgetType) {
    return new ConstantWidgetType(requireNotBlank(widgetType, "widgetType"));
  }

  private static final class ConstantWidgetTypeHolder {

    private static final WidgetType REFERRER = new ConstantWidgetType("REFERRER_WIDGET"),
        CONVERSION = new ConstantWidgetType("CONVERSION_WIDGET");

    private ConstantWidgetTypeHolder() {}

  }

}
