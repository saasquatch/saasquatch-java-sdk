package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.json.GsonUtils.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saasquatch.sdk.auth.AuthMethod;
import com.saasquatch.sdk.exceptions.SaaSquatchApiException;
import com.saasquatch.sdk.input.GetUserLinkInput;
import com.saasquatch.sdk.input.GraphQLInput;
import com.saasquatch.sdk.input.RenderWidgetInput;
import com.saasquatch.sdk.input.UserIdInput;
import com.saasquatch.sdk.input.WidgetType;
import com.saasquatch.sdk.input.WidgetUpsertInput;
import com.saasquatch.sdk.models.User;
import com.saasquatch.sdk.models.UserEventData;
import com.saasquatch.sdk.models.UserEventResult;
import com.saasquatch.sdk.output.ApiError;
import com.saasquatch.sdk.output.GraphQLApiResponse;
import com.saasquatch.sdk.output.GraphQLResult;
import com.saasquatch.sdk.output.JsonObjectApiResponse;
import com.saasquatch.sdk.output.TextApiResponse;
import com.saasquatch.sdk.test.IntegrationTestUtils;
import io.reactivex.rxjava3.core.Flowable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SaaSquatchClientIntegrationTest {

  private static SaaSquatchClient saasquatchClient;

  @BeforeAll
  public static void beforeAll() {
    IntegrationTestUtils.assumeCanRun();
    saasquatchClient = IntegrationTestUtils.newTestClient();
  }

  @AfterAll
  public static void afterAll() throws Exception {
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
      final JsonObjectApiResponse response =
          Flowable.fromPublisher(saasquatchClient.userUpsert(userInput, null)).blockingSingle();
      assertEquals(200, response.getHttpResponse().getStatusCode());
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
    try {
      Flowable.fromPublisher(saasquatchClient.userUpsert(
          userInput,
          RequestOptions.newBuilder().setAuthMethod(AuthMethod.ofTenantApiKey("fake")).build()))
          .blockingSubscribe();
    } catch (SaaSquatchApiException e) {
      final ApiError apiError = e.getApiError();
      assertEquals(401, apiError.getStatusCode());
      assertEquals(401, apiError.getStatusCode());
    }
    // Test tenantAlias override
    try {
      Flowable.fromPublisher(saasquatchClient.userUpsert(userInput,
          RequestOptions.newBuilder().setTenantAlias("fake").build())).blockingSubscribe();
    } catch (SaaSquatchApiException e) {
      final ApiError apiError = e.getApiError();
      assertEquals(404, apiError.getStatusCode());
      assertEquals(404, apiError.getStatusCode());
    }
  }

  @Test
  public void testBadUserUpsert() {
    final Map<String, Object> userInput = new HashMap<>();
    userInput.put("id", "asdf");
    userInput.put("accountId", "asdf");
    userInput.put("locale", "???");
    try {
      Flowable.fromPublisher(saasquatchClient.userUpsert(userInput, null)).blockingSubscribe();
    } catch (SaaSquatchApiException e) {
      final ApiError apiError = e.getApiError();
      assertEquals(400, apiError.getStatusCode());
      assertEquals(400, apiError.getStatusCode());
    }
  }

  @Test
  public void testWidgetRender() {
    upsertEmptyUser("asdf", "asdf");
    // Attempt to render a widget
    {
      final TextApiResponse response = Flowable
          .fromPublisher(saasquatchClient.renderWidget(RenderWidgetInput.newBuilder().setUser(
              UserIdInput.of("asdf", "asdf")).build(), null))
          .blockingSingle();
      assertEquals(200, response.getHttpResponse().getStatusCode());
      assertTrue(response.getData().toLowerCase(Locale.ROOT).contains("<!doctype html>"));
    }
    {
      final TextApiResponse response = Flowable
          .fromPublisher(saasquatchClient.renderWidget(RenderWidgetInput.newBuilder().setUser(
              UserIdInput.of("asdf", "asdf")).setWidgetType(WidgetType.classicConversionWidget())
              .build(), null)).blockingSingle();
      assertEquals(200, response.getHttpResponse().getStatusCode());
    }
  }

  @Test
  public void testBasicLogUserEvent() {
    upsertEmptyUser("asdf", "asdf");
    final Map<String, Object> userEventInput = new HashMap<>();
    userEventInput.put("userId", "asdf");
    userEventInput.put("accountId", "asdf");
    final Map<String, Object> fakeEvent = new HashMap<>();
    final Date dateTriggered = new Date();
    fakeEvent.put("key", "fake");
    fakeEvent.put("dateTriggered", dateTriggered);
    fakeEvent.put("fields", ImmutableMap.of("foo", "bar"));
    userEventInput.put("events", Arrays.asList(fakeEvent));
    {
      final JsonObjectApiResponse response = Flowable
          .fromPublisher(saasquatchClient.logUserEvent(userEventInput, null)).blockingSingle();
      assertEquals(200, response.getHttpResponse().getStatusCode());
      final UserEventResult model = response.toModel(UserEventResult.class);
      assertNotNull(model.getAccountId());
      assertNotNull(model.getUserId());
      final List<UserEventData> events = model.getEvents();
      assertEquals(1, events.size());
      final UserEventData eventData = events.get(0);
      assertEquals(dateTriggered.getTime(), eventData.getDateTriggered().getTime());
    }
  }

  @Test
  public void testWidgetUpsert() {
    final Map<String, Object> userInput = new HashMap<>();
    userInput.put("id", "asdf");
    userInput.put("accountId", "asdf");
    userInput.put("firstName", "Foo");
    userInput.put("lastName", "Bar");
    try {
      Flowable.fromPublisher(saasquatchClient.widgetUpsert(
          WidgetUpsertInput.newBuilder().setUserInput(userInput).build(),
          RequestOptions.newBuilder().addQueryParam("widgetType", "invalid").build()))
          .blockingSubscribe();
    } catch (SaaSquatchApiException e) {
      final ApiError apiError = e.getApiError();
      assertEquals(400, apiError.getStatusCode());
      assertEquals("RS036", apiError.getRsCode());
    }
  }

  @Test
  public void testGraphQLWorks() {
    final String query = "query { users { totalCount } }";
    final GraphQLApiResponse response =
        Flowable.fromPublisher(saasquatchClient.graphQL(GraphQLInput.ofQuery(query), null))
            .blockingSingle();
    assertEquals(200, response.getHttpResponse().getStatusCode());
    final GraphQLResult graphQLResult = response.getData();
    assertTrue(graphQLResult.getErrors() == null || graphQLResult.getErrors().isEmpty());
    final JsonObject dataJson = gson.toJsonTree(graphQLResult.getData()).getAsJsonObject();
    final JsonElement totalCountElem = dataJson.get("users").getAsJsonObject().get("totalCount");
    assertTrue(totalCountElem.isJsonPrimitive());
  }

  @Test
  public void testMessageLink() {
    final String messageLink =
        saasquatchClient.buildUserMessageLink(GetUserLinkInput.newBuilder().setAccountId("asdf")
            .setUserId("asdf").setProgramId("r1").setShareMedium("FACEBOOK").build(), null);
    assertEquals("https://staging.referralsaasquatch.com/a/"
        + IntegrationTestUtils.getTenantAlias()
        + "/message/redirect/FACEBOOK?accountId=asdf&userId=asdf&programId=r1", messageLink);
  }

  private void upsertEmptyUser(String accountId, String userId) {
    Flowable.fromPublisher(
        saasquatchClient.userUpsert(ImmutableMap.of("accountId", accountId, "id", userId), null))
        .blockingSubscribe();
  }

}
