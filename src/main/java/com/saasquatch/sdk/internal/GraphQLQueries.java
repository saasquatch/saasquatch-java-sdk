package com.saasquatch.sdk.internal;

public interface GraphQLQueries {

  String RENDER_WIDGET = ""
      + "query renderWidget(\n"
      + "  $user: UserIdInput\n"
      + "  $widgetType: WidgetType\n"
      + "  $engagementMedium: UserEngagementMedium\n"
      + "  $locale: RSLocale\n"
      + ") {\n"
      + "  renderWidget(\n"
      + "    user: $user\n"
      + "    widgetType: $widgetType\n"
      + "    engagementMedium: $engagementMedium\n"
      + "    locale: $locale\n"
      + "  ) {\n"
      + "    template\n"
      + "  }\n"
      + "}";

  String GET_WIDGET_CONFIG_VALUES = ""
      + "query renderWidget(\n"
      + "  $user: UserIdInput\n"
      + "  $widgetType: WidgetType\n"
      + "  $engagementMedium: UserEngagementMedium\n"
      + "  $locale: RSLocale\n"
      + ") {\n"
      + "  renderWidget(\n"
      + "    user: $user\n"
      + "    widgetType: $widgetType\n"
      + "    engagementMedium: $engagementMedium\n"
      + "    locale: $locale\n"
      + "  ) {\n"
      + "    widgetConfig {\n"
      + "      values\n"
      + "    }\n"
      + "  }\n"
      + "}";

  String CREATE_USER_ANALYTICS_EVENT = ""
      + "mutation createUserAnalyticsEvent(\n"
      + "  $eventMeta: UserAnalyticsEvent!\n"
      + ") {\n"
      + "  createUserAnalyticsEvent(\n"
      + "    eventMeta: $eventMeta\n"
      + "  )\n"
      + "}";

}
