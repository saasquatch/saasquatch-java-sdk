package com.saasquatch.sdk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.models.User;
import com.saasquatch.sdk.test.IntegrationTestUtils;
import io.reactivex.Flowable;

public class SaaSquatchClientIntegrationTest {

  private static SaaSquatchClient saasquatchClient;

  @BeforeAll
  public static void beforeAll() {
    IntegrationTestUtils.assumeCanRun();
    saasquatchClient = IntegrationTestUtils.newTestClient();
  }

  @AfterAll
  public static void afterAll() {
    if (saasquatchClient != null) {
      saasquatchClient.close();
    }
  }

  @Test
  public void testUserUpsert() {
    final Map<String, Object> userInput = new HashMap<>();
    assertThrows(NullPointerException.class, () -> saasquatchClient.userUpsert(userInput, null));
    userInput.put("id", "asdf");
    userInput.put("accountId", "asdf");
    userInput.put("firstName", "Foo");
    userInput.put("lastName", "Bar");
    {
      final MapApiResponse response = Flowable.fromPublisher(
          saasquatchClient.userUpsert(userInput, null)).blockingSingle();
      assertEquals(200, response.getStatusCode());
      final User user = response.toModel(User.class);
      assertNotNull(user);
      assertEquals("asdf", user.getId());
      assertEquals("asdf", user.getAccountId());
      assertEquals("Foo", user.getFirstName());
      assertEquals("Bar", user.getLastName());
      assertNotNull(user.getReferable());
      assertNotNull(user.getReferralCodes());
      assertNotNull(user.getSegments());
    }
    // Test auth override
    {
      final MapApiResponse response = Flowable.fromPublisher(
          saasquatchClient.userUpsert(userInput,
              RequestOptions.newBuilder().setAuthMethod(AuthMethod.ofApiKey("fake")).build()))
          .blockingSingle();
      assertEquals(401, response.getStatusCode());
      final ApiError apiError = response.getApiError();
      assertEquals(401, apiError.getStatusCode());
    }
    // Test tenantAlias override
    {
      final MapApiResponse response = Flowable.fromPublisher(
          saasquatchClient.userUpsert(userInput,
              RequestOptions.newBuilder().setTenantAlias("fake").build()))
          .blockingSingle();
      assertEquals(404, response.getStatusCode());
      final ApiError apiError = response.getApiError();
      assertEquals(404, apiError.getStatusCode());
    }
  }

  @Test
  public void testWidgetUpsert() {
    final Map<String, Object> userInput = new HashMap<>();
    userInput.put("id", "asdf");
    userInput.put("accountId", "asdf");
    userInput.put("firstName", "Foo");
    userInput.put("lastName", "Bar");
    final MapApiResponse response = Flowable.fromPublisher(
        saasquatchClient.widgetUpsert(userInput,
            RequestOptions.newBuilder()
                .addQueryParam("widgetType", "invalid widget type")
                .build()))
        .blockingSingle();
    assertEquals(400, response.getStatusCode());
    final ApiError apiError = response.getApiError();
    assertNotNull(apiError);
    assertEquals("RS036", apiError.getRsCode());
  }

}
