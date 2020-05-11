package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.InternalUtils.entryOf;
import static com.saasquatch.sdk.internal.InternalUtils.isBlank;
import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.internal.InternalUtils.unmodifiableList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.security.Permission;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PropertyPermission;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.junit.jupiter.api.Test;
import com.saasquatch.sdk.internal.InternalThreadFactory;
import com.saasquatch.sdk.internal.InternalUtils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
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

  @Test
  public void testRxExecuteRequestWorks() throws Exception {
    final ExecutorService executor =
        Executors.newCachedThreadPool(new InternalThreadFactory("foo"));
    try (CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.createDefault()) {
      httpAsyncClient.start();
      {
        final SimpleHttpRequest request = SimpleHttpRequests.get("https://example.com");
        final SimpleHttpResponse response =
            Flowable.fromPublisher(InternalUtils.executeRequest(httpAsyncClient, request))
                .blockingSingle();
        assertEquals(200, response.getCode());
      }
      // Test Reactive Streams implementation agnostic
      {
        final SimpleHttpRequest request = SimpleHttpRequests.get("https://example.com");
        final @NonNull SimpleHttpResponse response =
            Mono.from(InternalUtils.executeRequest(httpAsyncClient, request)).block();
        assertEquals(200, response.getCode());
      }
    } finally {
      executor.shutdown();
    }
  }

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

}
