package com.saasquatch.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

  /**
   * Convenience method for String.format with Locale.ROOT
   */
  public static String format(String format, Object... args) {
    return String.format(Locale.ROOT, format, args);
  }

  @Nonnull
  public static String buildUserAgent() {
    @Nullable
    final String javaVersion = System.getProperty("java.version");
    @Nonnull
    final String osName = System.getProperty("os.name", "");
    @Nonnull
    final String osVersion = System.getProperty("os.version", "");
    @Nonnull
    final String osStr = (osName + ' ' + osVersion).trim();
    return format("SaaSquatch SDK; %s; %s",
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
    }).toFlowable();
  }

  /**
   * Convenience method for {@link SimpleImmutableEntry}
   */
  public static <K, V> Map.Entry<K, V> entryOf(@Nullable K k, @Nullable V v) {
    return new SimpleImmutableEntry<>(k, v);
  }

  /**
   * Same as {@link Collections#unmodifiableList(List)}, but makes a defensive copy so modifying the
   * original list doesn't modify the unmoodifiable list.
   */
  @Nonnull
  public static <T> List<T> unmodifiableList(@Nonnull List<T> list) {
    switch (list.size()) {
      case 0:
        return Collections.emptyList();
      case 1:
        return Collections.singletonList(list.get(0));
      default:
        break;
    }
    @SuppressWarnings("unchecked")
    final List<T> defensiveCopy = (List<T>) Arrays.asList(list.toArray());
    return Collections.unmodifiableList(defensiveCopy);
  }

  /**
   * RFC3986 URL encode<br>
   * Note that this method has the functionality as RSUrlCodec in squatch-common and it's less
   * efficient, but the difference should be negligible for our use case. For now it's probably not
   * worth bringing in squatch-common as a dependency. If/when we do, we should replace this with
   * RSUrlCodec.
   */
  @Nonnull
  public static String urlEncode(@Nonnull String s) {
    try {
      return URLEncoder.encode(s, UTF_8.name())
          .replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // Seriously Java?
    }
  }

  /**
   * URL decode
   */
  @Nonnull
  public static String urlDecode(@Nonnull String s) {
    try {
      return URLDecoder.decode(s, UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // Facepalm
    }
  }

  @Nonnull
  public static String requireNotBlank(@Nullable String s, @Nonnull String msg) {
    if (Objects.requireNonNull(s, msg).trim().isEmpty()) {
      throw new IllegalArgumentException(msg);
    }
    return s;
  }

}
