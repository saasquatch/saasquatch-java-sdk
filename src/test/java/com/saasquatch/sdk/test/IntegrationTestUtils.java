package com.saasquatch.sdk.test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.util.Objects;
import java.util.stream.Stream;
import com.saasquatch.sdk.SaaSquatchClient;
import com.saasquatch.sdk.SaaSquatchClientOptions;

public class IntegrationTestUtils {

  private static final String CONST_BASE = "com.saasquatch.sdk.test.";
  public static final String APP_DOMAIN_PROP = CONST_BASE + "appDomain";
  public static final String TENANT_ALIAS_PROP = CONST_BASE + "tenantAlias";
  public static final String API_KEY_PROP = CONST_BASE + "apiKey";

  public static boolean canRun() {
    return Stream.of(getAppDomain(), getTenantAlias(), getApiKey()).allMatch(Objects::nonNull);
  }

  public static void assumeCanRun() {
    assumeTrue(canRun());
  }

  public static String getAppDomain() {
    return System.getProperty(APP_DOMAIN_PROP);
  }

  public static String getTenantAlias() {
    return System.getProperty(TENANT_ALIAS_PROP);
  }

  public static String getApiKey() {
    return System.getProperty(API_KEY_PROP);
  }

  public static SaaSquatchClient newTestClient() {
    return SaaSquatchClient.create(SaaSquatchClientOptions.newBuilder()
        .setTenantAlias(getTenantAlias())
        .setAppDomain(getAppDomain())
        .build());
  }

}
