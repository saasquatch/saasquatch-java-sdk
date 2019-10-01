package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.format;
import static com.saasquatch.sdk.InternalUtils.urlEncode;
import java.util.Objects;
import javax.annotation.concurrent.Immutable;

@Immutable
final class GAWidgetType implements WidgetType {

  private final String programId;
  private final String widgetKey;
  // Lazy init
  private String _toString;

  public GAWidgetType(String programId, String widgetKey) {
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
    String s = _toString;
    if (s == null) {
      _toString = s = format("p/%s/w/%s", urlEncode(programId), urlEncode(widgetKey));
    }
    return s;
  }

}
