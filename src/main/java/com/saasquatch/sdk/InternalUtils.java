package com.saasquatch.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    @Nullable
    final String javaVersion = System.getProperty("java.version");
    @Nonnull
    final String osName = System.getProperty("os.name", "");
    @Nonnull
    final String osVersion = System.getProperty("os.version", "");
    @Nonnull
    final String osStr = (osName + ' ' + osVersion).trim();
    return String.format("SaaSquatch SDK; %s; %s",
        javaVersion == null ? "Unknown Java version" : "Java " + javaVersion,
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
      final Call call = okHttpClient.newCall(request);
      call.enqueue(new okhttp3.Callback() {
        @Override
        public void onFailure(Call call, IOException ex) {
          emitter.onError(ex);
        }

        @Override
        public void onResponse(Call call, Response resp) throws IOException {
          emitter.onSuccess(resp);
        }
      });
      emitter.setCancellable(call::cancel);
    }).toFlowable();
  }

  public static <K, V> Map.Entry<K, V> entryOf(@Nullable K k, @Nullable V v) {
    return new SimpleImmutableEntry<>(k, v);
  }

  /**
   * Same as {@link Collections#unmodifiableList(List)}, but makes a defensive copy
   */
  public static <T> List<T> unmodifiableList(@Nonnull List<T> list) {
    if (list.isEmpty()) {
      return Collections.emptyList();
    }
    final Object[] arr = list.toArray();
    @SuppressWarnings("unchecked")
    final List<T> listCopy = (List<T>) Arrays.asList(arr);
    return Collections.unmodifiableList(listCopy);
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

  public static String requireNotBlank(@Nullable String s, @Nonnull String msg) {
    if (Objects.requireNonNull(s, msg).trim().isEmpty()) {
      throw new IllegalArgumentException(msg);
    }
    return s;
  }

}
