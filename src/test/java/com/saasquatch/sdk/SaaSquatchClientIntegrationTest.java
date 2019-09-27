package com.saasquatch.sdk;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.test.IntegrationTestUtils;

public class SaaSquatchClientIntegrationTest {

  private static SaaSquatchClient squatchClient;

  @BeforeAll
  public static void beforeAll() {
    IntegrationTestUtils.assumeCanRun();
    squatchClient = IntegrationTestUtils.newTestClient();
  }

  @AfterAll
  public static void afterAll() {
    squatchClient.close();
  }

  @Test
  public void foo() {
  }

}
