package com.saasquatch.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import okhttp3.Credentials;
import okhttp3.HttpUrl;
import okhttp3.Request;

public class SaaSquatchRequestOptions {

  private static final Set<String> BLOCKED_HEADERS;
  static {
    final Set<String> s = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
    s.addAll(Arrays.asList("Authorization", "Accept-Encoding", "Content-Encoding", "Content-Length",
        "Content-Type", "Accept", "Accept-Charset", "Cookie", "Set-Cookie", "Set-Cookie2",
        "Cache-Control", "Host", "X-SaaSquatch-User-Token"));
    BLOCKED_HEADERS = Collections.unmodifiableSet(s);
  }

  private final Map<String, String> singleHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
  private final List<Map.Entry<String, String>> multiHeaders = new ArrayList<>();
  private final List<Map.Entry<String, String>> queryParams = new ArrayList<>();

  public SaaSquatchRequestOptions() {}

  public SaaSquatchRequestOptions setApiKey(@Nonnull String apiKey) {
    Objects.requireNonNull(apiKey);
    singleHeaders.put("Authorization", Credentials.basic("", apiKey, UTF_8));
    return this;
  }

  public SaaSquatchRequestOptions setJwt(@Nonnull String jwt) {
    Objects.requireNonNull(jwt);
    singleHeaders.put("Authorization", "Bearer " + jwt);
    return this;
  }

  public SaaSquatchRequestOptions addHeader(@Nonnull String key, @Nonnull String value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    if (BLOCKED_HEADERS.contains(key)) {
      throw new IllegalArgumentException(key + " is not allowed");
    }
    multiHeaders.add(new SimpleImmutableEntry<>(key, value));
    return this;
  }

  public SaaSquatchRequestOptions addQueryParam(@Nonnull String key, @Nonnull String value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    queryParams.add(new SimpleImmutableEntry<>(key, value));
    return this;
  }

  void mutateRequest(Request.Builder requestBuilder, HttpUrl.Builder urlBuilder) {
    singleHeaders.forEach(requestBuilder::header);
    multiHeaders.forEach(e -> requestBuilder.addHeader(e.getKey(), e.getValue()));
    queryParams.forEach(e -> urlBuilder.addQueryParameter(e.getKey(), e.getValue()));
  }

}
