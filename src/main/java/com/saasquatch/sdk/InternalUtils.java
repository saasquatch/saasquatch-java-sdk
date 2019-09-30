package com.saasquatch.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

  public static <K, V> Map.Entry<K, V> entryOf(@Nullable K k, @Nullable V v) {
    return new SimpleImmutableEntry<>(k, v);
  }

  /**
   * RFC3986 URL encode<br>
   * Note that this method has the functionality as RSUrlCodec in squatch-common and it's less
   * efficient, but the difference should be negligible for our use case. For now it's probably not
   * worth bringing in squatch-common as a dependency. If/when we do, we should replace this with
   * RSUrlCodec.
   */
  public static String urlEncode(@Nonnull String s) {
    try {
      return URLEncoder.encode(s, UTF_8.name())
          .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // Should not happen
    }
  }

}
