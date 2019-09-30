package com.saasquatch.sdk;

import static com.saasquatch.sdk.test.IntegrationTestUtils.getApiKey;
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
    saasquatchClient.close();
  }

  @Test
  public void testUserUpsert() {
    final Map<String, Object> userInput = new HashMap<>();
    assertThrows(NullPointerException.class, () -> saasquatchClient.userUpsert(userInput, null));
    userInput.put("id", "asdf");
    userInput.put("accountId", "asdf");
    userInput.put("firstName", "Foo");
    userInput.put("lastName", "Bar");
    final SaaSquatchMapResponse response = Flowable.fromPublisher(
        saasquatchClient.userUpsert(userInput,
            SaaSquatchRequestOptions.newBuilder().setApiKey(getApiKey()).build()))
        .blockingSingle();
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

  @Test
  public void testWidgetUpsert() {
    final Map<String, Object> userInput = new HashMap<>();
    userInput.put("id", "asdf");
    userInput.put("accountId", "asdf");
    userInput.put("firstName", "Foo");
    userInput.put("lastName", "Bar");
    final SaaSquatchMapResponse response = Flowable.fromPublisher(
        saasquatchClient.widgetUpsert(userInput,
            SaaSquatchRequestOptions.newBuilder()
                .addQueryParam("widgetType", "invalid widget type")
                .setApiKey(getApiKey())
                .build()))
        .blockingSingle();
    assertEquals(400, response.getStatusCode());
    final SaaSquatchApiError apiError = response.getApiError();
    assertNotNull(apiError);
    assertEquals("RS036", apiError.getRsCode());
  }

}
