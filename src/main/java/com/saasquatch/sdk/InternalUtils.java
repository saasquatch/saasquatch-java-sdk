package com.saasquatch.sdk;

import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import javax.annotation.Nonnull;
import org.reactivestreams.Publisher;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class InternalUtils {

  public static String buildUserAgent() {
    final String javaVersion = System.getProperty("java.version", "Unknown");
    final String osName = System.getProperty("os.name", "");
    final String osVersion = System.getProperty("os.version", "");
    final String osStr = (osName + ' ' + osVersion).trim();
    return String.format("SaaSquatch SDK; %s; %s", "Java " + javaVersion,
        osStr.isEmpty() ? "Unknown OS" : osStr);
  }

  /**
   * Executes a non-blocking request
   *
   * @returns a {@link Publisher} that emits one element
   */
  public static Publisher<Response> executeRequest(@Nonnull OkHttpClient okHttpClient,
      @Nonnull Request request) {
    return Single.<Response>create(emitter -> {
      okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
        @Override
        public void onFailure(Call call, IOException ex) {
          emitter.onError(ex);
        }

        @Override
        public void onResponse(Call call, Response resp) throws IOException {
          emitter.onSuccess(resp);
        }
      });
    }).toFlowable();
  }

  public static <K, V> Map.Entry<K, V> entryOf(K k, V v) {
    return new SimpleImmutableEntry<>(k, v);
  }

}
