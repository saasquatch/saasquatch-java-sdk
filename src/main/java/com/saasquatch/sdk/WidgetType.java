package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.format;
import static com.saasquatch.sdk.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.InternalUtils.urlEncode;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import com.saasquatch.sdk.annotations.ClassicOnly;

/**
 * A SaaSquatch widget type
 *
 * @author sli
 * @see #of(String, String)
 * @see #ofClassic(String)
 */
@Immutable
public final class WidgetType {

  private final String themeWidgetType;
  private final String programId;
  private final String widgetKey;
  // Lazy init
  private String _toString;

  private WidgetType(String themeWidgetType, String programId, String widgetKey) {
    this.themeWidgetType = themeWidgetType;
    this.programId = programId;
    this.widgetKey = widgetKey;
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
    if (themeWidgetType != null) {
      return themeWidgetType;
    }
    String s = _toString;
    if (s == null) {
      _toString = s = format("p/%s/w/%s", urlEncode(programId), urlEncode(widgetKey));
    }
    return s;
  }

  /**
   * Create a {@link WidgetType} for the classic program
   */
  @ClassicOnly
  public static WidgetType ofClassic(@Nonnull String classicWidgetType) {
    return new WidgetType(requireNotBlank(classicWidgetType, "classicWidgetType"), null, null);
  }

  /**
   * Create a {@link WidgetType} for a program and a widget key
   */
  public static WidgetType of(@Nonnull String programId, @Nonnull String widgetKey) {
    return new WidgetType(null, requireNotBlank(programId, "programId"),
        requireNotBlank(widgetKey, "widgetKey"));
  }

}
