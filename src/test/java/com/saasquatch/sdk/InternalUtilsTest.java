package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.entryOf;
import static com.saasquatch.sdk.InternalUtils.requireNotBlank;
import static com.saasquatch.sdk.InternalUtils.unmodifiableList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import reactor.core.publisher.Mono;

public class InternalUtilsTest {

  @Test
  public void testBuildUserAgent() {
    final String userAgent = InternalUtils.buildUserAgent(UUID.randomUUID().toString());
    assertTrue(userAgent.startsWith("SaaSquatch SDK"));
  }

  @Test
  public void testEntryOf() {
    final Object k = new Object();
    final Object v = new Object();
    assertEquals(new SimpleImmutableEntry<>(k, v), entryOf(k, v));
    assertEquals(new SimpleEntry<>(k, v), entryOf(k, v));
  }

  @Test
  public void testRxExecuteRequestWorks() {
    final ExecutorService executor = Executors.newCachedThreadPool(InternalThreadFactory.INSTANCE);
    try {
      final OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .dispatcher(new okhttp3.Dispatcher(executor))
          .build();
      {
        final Request request = new Request.Builder().url("https://example.com").get().build();
        final Response response =
            Flowable.fromPublisher(InternalUtils.executeRequest(okHttpClient, request))
                .blockingSingle();
        assertEquals(200, response.code());
      }
      // Test Reactive Streams implementation agnostic
      {
        final Request request = new Request.Builder().url("https://example.com").get().build();
        final Response response =
            Mono.from(InternalUtils.executeRequest(okHttpClient, request)).block();
        assertEquals(200, response.code());
      }
    } finally {
      executor.shutdown();
    }
  }

  @Test
  public void testUnmodifiableList() {
    final List<Integer> list = Arrays.asList(1, 2, 3);
    final List<Integer> unmodifiableList = unmodifiableList(list);
    list.set(0, 2);
    assertEquals(Arrays.asList(2, 2, 3), list);
    assertEquals(Arrays.asList(1, 2, 3), unmodifiableList);
  }

  @Test
  public void testRequireNotBlank() {
    assertThrows(NullPointerException.class, () -> requireNotBlank(null, ""));
    assertDoesNotThrow(() -> requireNotBlank("foo", null));
    assertThrows(IllegalArgumentException.class, () -> requireNotBlank("\t", "\t"));
  }

}
