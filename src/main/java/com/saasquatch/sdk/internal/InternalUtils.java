package com.saasquatch.sdk.internal;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.reactivestreams.Publisher;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public final class InternalUtils {

  private static final Map<String, String> RFC_3986_REPLACEMENTS;
  static {
    final Map<String, String> m = new HashMap<>(4);
    m.put("+", "%20");
    m.put("*", "%2A");
    m.put("%7E", "~");
    RFC_3986_REPLACEMENTS = Collections.unmodifiableMap(m);
  }

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
    final String osStr = getSysProp("os.name", "") + ' ' + getSysProp("os.version", "");
    final String osArch = getSysProp("os.arch", "");
    final StringBuilder uaBuilder = new StringBuilder(128);
    uaBuilder.append("SaaSquatch SDK");
    uaBuilder.append(" (");
    uaBuilder.append(isBlank(javaVersion) ? "Unknown Java version" : "Java " + javaVersion);
    uaBuilder.append("; ");
    uaBuilder.append(isBlank(osStr) ? "Unknown OS" : osStr.trim());
    uaBuilder.append("; ");
    uaBuilder.append(isBlank(osArch) ? "Unknown Arch" : osArch);
    uaBuilder.append("; ");
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

  public static <T> Set<T> unmodifiableSet(@Nonnull Set<T> set) {
    switch (set.size()) {
      case 0:
        return Collections.emptySet();
      case 1:
        return Collections.singleton(set.iterator().next());
      default:
        break;
    }
    return Collections.unmodifiableSet(new LinkedHashSet<>(set));
  }

  public static <K, V> Map<K, V> unmodifiableMap(@Nonnull Map<K, V> map) {
    switch (map.size()) {
      case 0:
        return Collections.emptyMap();
      case 1: {
        final Map.Entry<K, V> singleEntry = map.entrySet().iterator().next();
        return Collections.singletonMap(singleEntry.getKey(), singleEntry.getValue());
      }
      default:
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }
  }

  /**
   * RFC3986 URL encode<br>
   * Note that this method has the same functionality as RSUrlCodec in squatch-common and it's less
   * efficient, but the difference should be negligible for our use case. For now it's probably not
   * worth bringing in squatch-common as a dependency. If/when we do, we should replace this with
   * RSUrlCodec.
   */
  @Nonnull
  public static String urlEncode(@Nonnull String s) {
    try {
      return stringReplace(URLEncoder.encode(s, UTF_8.name()), RFC_3986_REPLACEMENTS);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // Seriously Java?
    }
  }

  /**
   * More efficient String replace without regex.
   */
  public static String stringReplace(String string, Map<String, String> replacementMap) {
    final StringBuilder sb = new StringBuilder(string);
    for (Map.Entry<String, String> entry : replacementMap.entrySet()) {
      final String key = entry.getKey();
      final String value = entry.getValue();
      int start = sb.indexOf(key, 0);
      while (start > -1) {
        final int end = start + key.length();
        final int nextSearchStart = start + value.length();
        sb.replace(start, end, value);
        start = sb.indexOf(key, nextSearchStart);
      }
    }
    return sb.toString();
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

  public static boolean isBlank(@Nonnull CharSequence s) {
    if (s == null) {
      return true;
    }
    final int len = s.length();
    if (len == 0) {
      return true;
    }
    for (int i = 0; i < len; i++) {
      if (!Character.isWhitespace(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  @Nonnull
  public static String requireNotBlank(@Nullable String s, @Nullable String msg) {
    if (msg == null) {
      Objects.requireNonNull(s);
    } else {
      Objects.requireNonNull(s, msg);
    }
    if (isBlank(s)) {
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

  public static byte[] toByteArray(InputStream in) throws IOException {
    try (ByteArrayOutputStream baOut = new ByteArrayOutputStream()) {
      final byte[] buf = new byte[8192];
      int bytesRead;
      while ((bytesRead = in.read(buf)) >= 0) {
        baOut.write(buf, 0, bytesRead);
      }
      return baOut.toByteArray();
    }
  }

  public static byte[] getBodyBytes(SimpleHttpResponse response) {
    final byte[] bodyBytes = response.getBodyBytes();
    if (bodyBytes == null) {
      return null;
    }
    final Header contentEncodingHeader = response.getFirstHeader(HttpHeaders.CONTENT_ENCODING);
    final String contentEncoding =
        contentEncodingHeader == null ? null : contentEncodingHeader.getValue();
    if ("gzip".equalsIgnoreCase(contentEncoding)) {
      try (GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(bodyBytes))) {
        return toByteArray(gzipIn);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      return bodyBytes;
    }
  }

  public static String getBodyText(SimpleHttpResponse response) {
    final byte[] bodyBytes = getBodyBytes(response);
    if (bodyBytes == null) {
      return null;
    }
    final ContentType contentType = response.getContentType();
    final Charset charset = contentType == null ? null : contentType.getCharset();
    return new String(bodyBytes, charset == null ? UTF_8 : charset);
  }

}
