package com.saasquatch.sdk.test;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import com.google.common.collect.ImmutableSet;
import com.saasquatch.sdk.ClientOptions;
import com.saasquatch.sdk.SaaSquatchClient;
import com.saasquatch.sdk.auth.AuthMethods;

public class IntegrationTestUtils {

  private static final String PROD_APP_DOMAIN = "app.referralsaasquatch.com";
  private static final String CONST_BASE = "com.saasquatch.sdk.test.";
  public static final String APP_DOMAIN_PROP = CONST_BASE + "appDomain";
  public static final String TENANT_ALIAS_PROP = CONST_BASE + "tenantAlias";
  public static final String TENANT_API_KEY_PROP = CONST_BASE + "tenantApiKey";
  public static final Set<String> REQUIRED_SYSTEM_PROPERTIES =
      ImmutableSet.of(APP_DOMAIN_PROP, TENANT_ALIAS_PROP, TENANT_API_KEY_PROP);

  public static boolean canRun() {
    return Stream.of(getAppDomain(), getTenantAlias(), getTenantApiKey())
        .allMatch(Objects::nonNull);
  }

  public static void assumeCanRun() {
    final boolean canRun = canRun();
    if (!canRun) {
      System.out.printf(Locale.ROOT,
          "Not all of %s system properties are present. Skipping integration tests.\n",
          REQUIRED_SYSTEM_PROPERTIES);
    }
    assumeTrue(canRun);
  }

  public static String getAppDomain() {
    final String appDomain = System.getProperty(APP_DOMAIN_PROP);
    if (appDomain != null && appDomain.toLowerCase(Locale.ROOT).contains(PROD_APP_DOMAIN)) {
      System.out.println("Running tests against the production app is not allowed!!!");
      return null;
    }
    return appDomain;
  }

  public static String getTenantAlias() {
    return System.getProperty(TENANT_ALIAS_PROP);
  }

  public static String getTenantApiKey() {
    return System.getProperty(TENANT_API_KEY_PROP);
  }

  public static SaaSquatchClient newTestClient() {
    return SaaSquatchClient.create(
        ClientOptions.newBuilder().setTenantAlias(getTenantAlias()).setAppDomain(getAppDomain())
            .setAuthMethod(AuthMethods.ofTenantApiKey(getTenantApiKey())).build());
  }

}
