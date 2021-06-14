package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.InternalUtils.defaultIfNull;
import static com.saasquatch.sdk.internal.InternalUtils.entryOf;
import static com.saasquatch.sdk.internal.InternalUtils.getJwtPayload;
import static com.saasquatch.sdk.internal.InternalUtils.getNestedMapValue;
import static com.saasquatch.sdk.internal.InternalUtils.getUserIdInputFromUserJwt;
import static com.saasquatch.sdk.internal.InternalUtils.isBlank;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableList;
import static com.saasquatch.sdk.internal.json.GsonUtils.gson;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableMap;
import com.saasquatch.sdk.input.UserIdInput;
import com.saasquatch.sdk.internal.InternalUtils;
import io.reactivex.rxjava3.core.Flowable;
import java.io.ByteArrayInputStream;
import java.security.Permission;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PropertyPermission;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class InternalUtilsTest {

  @Test
  public void testBuildUserAgent() {
    final String userAgent = InternalUtils.buildUserAgent("foo");
    assertTrue(userAgent.startsWith("SaaSquatch SDK"));
  }

  @Test
  public void testGetSysProp() {
    final SecurityManager customSecurityManager = new SecurityManager() {

      @Override
      public void checkPermission(Permission perm) {
        if (perm instanceof PropertyPermission) {
          throw new SecurityException(perm.getName());
        }
      }

    };
    System.setSecurityManager(customSecurityManager);
    try {
      assertThrows(SecurityException.class, () -> System.getProperty("someRandomProperty"));
      assertEquals("foo", InternalUtils.getSysProp("someRandomProperty", "foo"));
    } finally {
      System.setSecurityManager(null);
    }
  }

  @Test
  public void testEntryOf() {
    final Object k = new Object();
    final Object v = new Object();
    assertEquals(new SimpleImmutableEntry<>(k, v), entryOf(k, v));
    assertEquals(new SimpleEntry<>(k, v), entryOf(k, v));
  }

  @SuppressWarnings("ConstantConditions")
  @Test
  public void testRxExecuteRequestWorks() throws Exception {
    try (CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.createDefault()) {
      httpAsyncClient.start();
      {
        final SimpleHttpRequest request = SimpleRequestBuilder.get("https://example.com").build();
        final SimpleHttpResponse response = Flowable
            .fromPublisher(InternalUtils.executeRequest(httpAsyncClient, request)).blockingSingle();
        assertEquals(200, response.getCode());
      }
      // Test Reactive Streams implementation agnostic
      {
        final SimpleHttpRequest request = SimpleRequestBuilder.get("https://example.com").build();
        final SimpleHttpResponse response =
            Mono.from(InternalUtils.executeRequest(httpAsyncClient, request)).block();
        assertEquals(200, response.getCode());
      }
    }
  }

  @SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
  @Test
  public void testUnmodifiableList() {
    {
      final List<Integer> list = Arrays.asList(1, 2, 3);
      final List<Integer> unmodifiableList = unmodifiableList(list);
      list.set(0, 2);
      assertEquals(Arrays.asList(2, 2, 3), list);
      assertEquals(Arrays.asList(1, 2, 3), unmodifiableList);
    }
    assertSame(Collections.emptyList(), unmodifiableList(Arrays.asList()));
    assertEquals("SingletonList", unmodifiableList(Arrays.asList(1)).getClass().getSimpleName());
  }

  @Test
  public void testRequireNotBlank() {
    assertThrows(NullPointerException.class, () -> requireNotBlank(null, ""));
    assertDoesNotThrow(() -> requireNotBlank("foo", null));
    assertThrows(IllegalArgumentException.class, () -> requireNotBlank("\t", "\t"));
  }

  @Test
  public void testBlank() {
    assertTrue(isBlank(null));
    assertTrue(isBlank(""));
    assertTrue(isBlank(" \t"));
    assertFalse(isBlank(" a \r"));
  }

  @Test
  public void testInputStreamToByteArray() throws Exception {
    final String s = InternalUtils.randomHexString(65536);
    assertEquals(s,
        new String(InternalUtils.toByteArray(new ByteArrayInputStream(s.getBytes(UTF_8))), UTF_8));
  }

  @Test
  public void testDefaultIfNull() {
    {
      final Object o1 = new Object();
      final Object o2 = new Object();
      assertSame(o1, defaultIfNull(o1, o2));
      assertSame(o1, defaultIfNull(o1, null));
      assertSame(o2, defaultIfNull(null, o2));
      //noinspection ConstantConditions
      assertNull(defaultIfNull(null, null));
    }
  }

  @Test
  public void testGetNestedMapValue() {
    assertNull(getNestedMapValue(null));
    assertNull(getNestedMapValue(null, "a", "b"));
    final Map<String, Object> m = ImmutableMap
        .of("a", ImmutableMap.of("b", ImmutableMap.of("c", true)));
    assertSame(m, getNestedMapValue(m));
    assertEquals(true, getNestedMapValue(m, "a", "b", "c"));
    assertNull(getNestedMapValue(m, "a", "c"));
    assertThrows(ClassCastException.class, () -> getNestedMapValue(m, "a", "b", "c", "d"));
    {
      final Object value = getNestedMapValue(m, "a", "b");
      assertEquals("{\"c\":true}", gson.toJson(value));
    }
  }

  @Test
  public void testGetJwtPayload() {
    //noinspection ConstantConditions
    assertThrows(NullPointerException.class, () -> getJwtPayload(null));
    assertThrows(IllegalArgumentException.class, () -> getJwtPayload("foo"));
    assertThrows(IllegalArgumentException.class, () -> getJwtPayload("a.b.c"));
    assertThrows(IllegalArgumentException.class, () -> getJwtPayload("a.e30.c.d"));
    assertThrows(IllegalArgumentException.class, () -> getJwtPayload("a.e30.c.d.e"));
    assertEquals(ImmutableMap.of(), getJwtPayload("a.e30.c"));
  }

  @Test
  public void testJwtToUserIdInput() {
    final UserIdInput userIdInput = getUserIdInputFromUserJwt(
        "a.eyJ1c2VyIjp7ImlkIjoiYiIsImFjY291bnRJZCI6ImEiLCJlbWFpbCI6ImFAZXhhbXBsZS5jb20ifX0.a");
    assertEquals("a", userIdInput.getAccountId());
    assertEquals("b", userIdInput.getId());
  }

}
