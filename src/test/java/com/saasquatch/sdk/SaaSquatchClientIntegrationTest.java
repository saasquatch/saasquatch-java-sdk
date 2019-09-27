package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.BeforeAll;
import com.saasquatch.sdk.test.IntegrationTestUtils;

public class SaaSquatchClientIntegrationTest {

  @BeforeAll
  public static void beforeAll() {
    assumeTrue(IntegrationTestUtils.canRun());
  }

}
