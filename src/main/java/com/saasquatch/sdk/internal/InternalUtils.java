package com.saasquatch.sdk.internal;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.reactivestreams.Publisher;
import com.saasquatch.sdk.annotations.Internal;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Internal
public final class InternalUtils {

  private InternalUtils() {}

  /**
   * Convenience method for String.format with Locale.ROOT
   */
  public static String format(@Nonnull String format, Object... args) {
    return String.format(Locale.ROOT, format, args);
  }

  @Nonnull
  public static String buildUserAgent(@Nonnull String clientId) {
    final String javaVersion = getSysProp("java.version", "");
    final String osName = getSysProp("os.name", "");
    final String osVersion = getSysProp("os.version", "");
    final String osArch = getSysProp("os.arch", "");
    final String osStr = (osName + ' ' + osVersion + ' ' + osArch).trim();
    final StringBuilder uaBuilder = new StringBuilder("SaaSquatch SDK (");
    uaBuilder.append(javaVersion.isEmpty() ? "Unknown Java version" : "Java " + javaVersion);
    uaBuilder.append("; ");
    uaBuilder.append(osStr.isEmpty() ? "Unknown OS" : osStr).append("; ");
    uaBuilder.append(clientId);
    uaBuilder.append(')');
    return uaBuilder.toString();
  }

  /**
   * Same as {@link System#getProperty(String, String)}, but falls back to the default when it
   * errors.
   */
  public static String getSysProp(@Nonnull String key, @Nullable String def) {
    try {
      return System.getProperty(key, def);
    } catch (Throwable t) {
      return def;
    }
  }

  /**
   * Executes a non-blocking request
   *
   * @returns a {@link Publisher} that emits one element
   */
  public static Flowable<SimpleHttpResponse> executeRequest(
      @Nonnull CloseableHttpAsyncClient httpAsyncClient, @Nonnull SimpleHttpRequest request) {
    return Single.<SimpleHttpResponse>create(emitter -> {
      httpAsyncClient.execute(request, new FutureCallback<SimpleHttpResponse>() {

        @Override
        public void failed(Exception ex) {
          emitter.onError(ex);
        }

        @Override
        public void completed(SimpleHttpResponse result) {
          emitter.onSuccess(result);
        }

        @Override
        public void cancelled() {
          emitter.onError(new CancellationException());
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
   * (Almost) RFC3986 URL encode<br>
   * Note that this method has the same functionality as RSUrlCodec in squatch-common and it's less
   * efficient, but the difference should be negligible for our use case. For now it's probably not
   * worth bringing in squatch-common as a dependency. If/when we do, we should replace this with
   * RSUrlCodec.
   */
  @Nonnull
  public static String urlEncode(@Nonnull String s) {
    try {
      return URLEncoder.encode(s, UTF_8.name()).replace("+", "%20").replace("*", "%2A");
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
  public static String requireNotBlank(@Nullable String s, @Nullable String msg) {
    if (msg == null) {
      Objects.requireNonNull(s);
    } else {
      Objects.requireNonNull(s, msg);
    }
    if (s.trim().isEmpty()) {
      if (msg == null) {
        throw new IllegalArgumentException();
      } else {
        throw new IllegalArgumentException(msg);
      }
    }
    return s;
  }

  public static String randomHexString(int length) {
    final CharBuffer buf = CharBuffer.allocate(length);
    while (buf.hasRemaining()) {
      buf.put(Character.forDigit(ThreadLocalRandom.current().nextInt(16), 16));
    }
    buf.flip();
    return buf.toString();
  }

}
