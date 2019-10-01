package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.requireNotBlank;
import javax.annotation.Nonnull;

/**
 * A SaaSquatch widget type
 *
 * @author sli
 * @see #of(String, String)
 * @see ClassicWidgetType
 */
public interface WidgetType {

  /**
   * Create a {@link WidgetType} for a program and a widget key
   */
  public static WidgetType of(@Nonnull String programId, @Nonnull String widgetKey) {
    return new GAWidgetType(requireNotBlank(programId, "programId"),
        requireNotBlank(widgetKey, "widgetKey"));
  }

}
