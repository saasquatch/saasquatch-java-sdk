package com.saasquatch.sdk.input;

import static com.saasquatch.sdk.internal.InternalUtils.format;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.urlEncode;
import java.util.Objects;
import javax.annotation.Nonnull;

public final class GAWidgetType implements WidgetType {

  private final String programId;
  private final String widgetKey;
  // Lazy init
  private String _widgetType;

  private GAWidgetType(String programId, String widgetKey) {
    this.programId = programId;
    this.widgetKey = widgetKey;
  }

  @Override
  public String getWidgetType() {
    String s = _widgetType;
    if (s == null) {
      _widgetType = s = format("p/%s/w/%s", urlEncode(programId), urlEncode(widgetKey));
    }
    return s;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final WidgetType other = (WidgetType) obj;
    return Objects.equals(this.toString(), other.toString());
  }

  @Override
  public String toString() {
    return getWidgetType();
  }

  /**
   * Create a {@link WidgetType} for a program and a widget key
   */
  public static WidgetType of(@Nonnull String programId, @Nonnull String widgetKey) {
    return new GAWidgetType(requireNotBlank(programId, "programId"),
        requireNotBlank(widgetKey, "widgetKey"));
  }

}
