package com.saasquatch.sdk.internal;

import static com.saasquatch.sdk.internal.json.GsonUtils.gson;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saasquatch.sdk.exceptions.SaaSquatchApiException;
import com.saasquatch.sdk.exceptions.SaaSquatchUnhandledApiException;
import com.saasquatch.sdk.input.UserIdInput;
import com.saasquatch.sdk.output.ApiError;
import com.saasquatch.sdk.output.GraphQLApiResponse;
import com.saasquatch.sdk.output.GraphQLResult;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.zip.GZIPInputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.WillNotClose;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.MessageHeaders;
import org.reactivestreams.Publisher;

public final class InternalUtils {

  public static final String GZIP = "gzip";
  private static final int BUFFER_SIZE = 8192;

  /**
   * RFC-3986 unreserved characters used for standard URL encoding.<br>
   * <a href="https://tools.ietf.org/html/rfc3986#section-2.3">Source</a>
   */
  private static final BitSet RFC_3986_SAFE_CHARS;

  static {
    RFC_3986_SAFE_CHARS = new BitSet(256);
    RFC_3986_SAFE_CHARS.set('a', 'z' + 1);
    RFC_3986_SAFE_CHARS.set('A', 'Z' + 1);
    RFC_3986_SAFE_CHARS.set('0', '9' + 1);
    RFC_3986_SAFE_CHARS.set('-');
    RFC_3986_SAFE_CHARS.set('_');
    RFC_3986_SAFE_CHARS.set('.');
    RFC_3986_SAFE_CHARS.set('~');
  }

  private InternalUtils() {}

  /**
   * Convenience method for String.format with Locale.ROOT
   */
  public static String format(@Nonnull String format, Object... args) {
    return String.format(Locale.ROOT, format, args);
  }

  public static String buildUserAgent(@Nonnull String clientId) {
    final String javaVersion = getSysProp("java.version", "");
    final String osStr = getSysProp("os.name", "") + ' ' + getSysProp("os.version", "");
    final String osArch = getSysProp("os.arch", "");
    return "SaaSquatch SDK ("
        + (isBlank(javaVersion) ? "Unknown Java version" : "Java " + javaVersion)
        + "; "
        + (isBlank(osStr) ? "Unknown OS" : osStr.trim())
        + "; "
        + (isBlank(osArch) ? "Unknown Arch" : osArch)
        + "; "
        + clientId
        + ')';
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
   * Get the system property or env variable by a key
   */
  @Nullable
  public static String getSysPropOrEnv(@Nonnull String key) {
    String result = null;
    try {
      result = System.getProperty(key);
    } catch (Throwable t) {
      // ignore
    }
    if (result == null) {
      try {
        result = System.getenv(key);
      } catch (Throwable t) {
        // ignore
      }
    }
    return result;
  }

  /**
   * Executes a non-blocking request
   *
   * @return a {@link Publisher} that emits one element
   */
  public static Flowable<SimpleHttpResponse> executeRequest(
      @Nonnull CloseableHttpAsyncClient httpAsyncClient, @Nonnull SimpleHttpRequest request) {
    //noinspection CodeBlock2Expr
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
   * Collect all the headers in the given {@link MessageHeaders} into an immutable {@link Map} where
   * the values are also immutable.
   */
  public static Map<String, List<String>> collectHeaders(@Nonnull MessageHeaders messageHeaders) {
    final Map<String, List<String>> result = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    final Iterator<Header> headerIterator = messageHeaders.headerIterator();
    while (headerIterator.hasNext()) {
      final Header header = headerIterator.next();
      List<String> values = result.get(header.getName());
      // For Android
      //noinspection Java8MapApi
      if (values == null) {
        values = new ArrayList<>();
        result.put(header.getName(), values);
      }
      values.add(header.getValue());
    }
    final Map<String, List<String>> resultCopy = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    for (Map.Entry<String, List<String>> e : result.entrySet()) {
      resultCopy.put(e.getKey(), unmodifiableList(e.getValue()));
    }
    // DO NOT use InternalUtils.unmodifiableMap
    return Collections.unmodifiableMap(resultCopy);
  }

  /**
   * Convenience method for {@link SimpleImmutableEntry}
   */
  public static <K, V> Map.Entry<K, V> entryOf(@Nullable K k, @Nullable V v) {
    return new SimpleImmutableEntry<>(k, v);
  }

  /**
   * Same as {@link Collections#unmodifiableList(List)}, but makes a defensive copy so modifying the
   * original list doesn't modify the unmodifiable list.
   */
  public static <T> List<T> unmodifiableList(@Nonnull List<T> list) {
    switch (list.size()) {
      case 0:
        return Collections.emptyList();
      case 1:
        return Collections.singletonList(list.get(0));
      default:
        break;
    }
    @SuppressWarnings("unchecked") final List<T> defensiveCopy =
        (List<T>) Arrays.asList(list.toArray());
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
        break;
    }
    return Collections.unmodifiableMap(new LinkedHashMap<>(map));
  }

  /**
   * RFC3986 URL encode
   */
  public static String urlEncode(@Nonnull String s) {
    return new String(URLCodec.encodeUrl(RFC_3986_SAFE_CHARS, s.getBytes(UTF_8)), UTF_8);
  }

  /**
   * URL decode
   */
  public static String urlDecode(@Nonnull String s) {
    try {
      return URLDecoder.decode(s, UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e); // Facepalm
    }
  }

  public static boolean isBlank(@Nullable CharSequence s) {
    final int len;
    if (s == null || (len = s.length()) == 0) {
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

  public static byte[] toByteArray(@WillNotClose InputStream in) throws IOException {
    try (ByteArrayOutputStream baOut = new ByteArrayOutputStream()) {
      final byte[] buf = new byte[BUFFER_SIZE];
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
    if (GZIP.equalsIgnoreCase(contentEncoding)) {
      try (GZIPInputStream gzipIn = new GZIPInputStream(
          new ByteArrayInputStream(bodyBytes), BUFFER_SIZE)) {
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

  public static <T> T defaultIfNull(T item, T fallback) {
    return item == null ? fallback : item;
  }

  public static void throwSquatchExceptionForPotentialGraphQLError(
      @Nonnull GraphQLApiResponse graphQLApiResponse) {
    final GraphQLResult graphQLResult = graphQLApiResponse.getData();
    if (graphQLResult == null) {
      throw new SaaSquatchUnhandledApiException(graphQLApiResponse.getHttpResponse());
    }
    final ApiError graphQLApiError = graphQLResult.getGraphQLApiError();
    if (graphQLApiError != null) {
      throw new SaaSquatchApiException(graphQLApiError, graphQLApiResponse.getHttpResponse());
    } else if (graphQLResult.getErrors() != null && !graphQLResult.getErrors().isEmpty()) {
      throw new SaaSquatchUnhandledApiException(graphQLApiResponse.getHttpResponse());
    }
  }

  public static Object getNestedMapValue(Map<String, Object> map, String... keys) {
    Object result = map;
    for (String key : keys) {
      if (result == null) {
        return null;
      }
      @SuppressWarnings("unchecked") final Map<String, Object> resultAsMap =
          (Map<String, Object>) result;
      result = resultAsMap.get(key);
    }
    return result;
  }

  /**
   * Extract the payload as a JSON object. This method does NOT do a full JWT validation.
   */
  public static Map<String, Object> getJwtPayload(String jwt) {
    final String[] jwtParts = jwt.split("\\.", 4);
    if (jwtParts.length != 3) {
      throw new IllegalArgumentException("Invalid JWT");
    }
    final String payloadPart = jwtParts[1];
    // Do not use the overload that takes a String. It does not work on Android.
    final byte[] payloadBytes = Base64.decodeBase64(payloadPart.getBytes(UTF_8));
    final JsonElement jsonElement = JsonParser.parseString(new String(payloadBytes, UTF_8));
    if (!(jsonElement instanceof JsonObject)) {
      throw new IllegalArgumentException("JWT payload is not a JSON object");
    }
    @SuppressWarnings("unchecked") final Map<String, Object> payloadMap =
        gson.fromJson(jsonElement, Map.class);
    return payloadMap;
  }

  @Nonnull
  public static UserIdInput getUserIdInputFromUserJwt(String userJwt) {
    final Map<String, Object> payload = getJwtPayload(userJwt);
    //noinspection ConstantConditions
    return UserIdInput.of((String) getNestedMapValue(payload, "user", "accountId"),
        (String) getNestedMapValue(payload, "user", "id"));
  }

}
