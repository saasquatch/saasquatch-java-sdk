package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.urlEncode;
import javax.annotation.Nonnull;

final class ProgramWidgetType implements WidgetType {

  // Lazy init
  private String widgetType;
  private final String programId;
  private final String programWidgetKey;

  ProgramWidgetType(@Nonnull String programId, @Nonnull String programWidgetKey) {
    this.programId = programId;
    this.programWidgetKey = programWidgetKey;
  }

  @Nonnull
  @Override
  public String getWidgetType() {
    String t = widgetType;
    if (t == null) {
      widgetType = t = "p/" + urlEncode(programId) + "/w/" + urlEncode(programWidgetKey);
    }
    return t;
  }

  @Nonnull
  @Override
  public String getProgramId() {
    return programId;
  }

}
