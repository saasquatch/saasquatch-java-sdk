package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.urlEncode;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.SaaSquatchClient;
import com.saasquatch.sdk.annotations.ClassicOnly;
import com.saasquatch.sdk.annotations.Internal;

/**
 * A SaaSquatch widget type
 *
 * @author sli
 * @see SaaSquatchClient#renderWidget(String, String, WidgetType, com.saasquatch.sdk.RequestOptions)
 * @see SaaSquatchClient#widgetUpsert(UserInput, WidgetType, com.saasquatch.sdk.RequestOptions)
 */
public final class WidgetType {

  private final String widgetType;

  private WidgetType(String widgetType) {
    this.widgetType = widgetType;
  }

  @Internal
  public String getWidgetType() {
    return widgetType;
  }

  public static WidgetType of(String widgetType) {
    return new WidgetType(requireNotBlank(widgetType, "widgetType"));
  }

  @ClassicOnly
  public static WidgetType referrerWidget() {
    return ConstantWidgetTypeHolder.REFERRER;
  }

  @ClassicOnly
  public static WidgetType conversionWidget() {
    return ConstantWidgetTypeHolder.CONVERSION;
  }

  public static WidgetType ofProgramWidget(@Nonnull String programId, @Nonnull String widgetKey) {
    requireNotBlank(programId, "programId");
    requireNotBlank(widgetKey, "widgetKey");
    return of("p/" + urlEncode(programId) + "/w/" + urlEncode(widgetKey));
  }

  private static final class ConstantWidgetTypeHolder {

    private static final WidgetType REFERRER = of("REFERRER_WIDGET"),
        CONVERSION = of("CONVERSION_WIDGET");

    private ConstantWidgetTypeHolder() {}

  }

}
