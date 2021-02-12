package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.json.GsonUtils.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.saasquatch.sdk.internal.json.GsonSerializeNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GsonUtilsTest {

  @Test
  public void testDateSerialization() {
    final Map<String, Object> m = Collections.singletonMap("foo", new Date(123));
    assertEquals("{\"foo\":123}", gson.toJson(m));
  }

  @Test
  public void testDateDeserialization() {
    final FooDateDto dto = gson.fromJson("{\"foo\":123}", FooDateDto.class);
    assertEquals(new Date(123), dto.foo);
  }

  @Test
  public void testNullDateDeserialization() {
    final FooDateDto dto = gson.fromJson("{\"foo\":null}", FooDateDto.class);
    assertNull(dto.foo);
  }

  static class FooDateDto {

    public final Date foo;

    public FooDateDto(Date foo) {
      this.foo = foo;
    }

  }

  @Test
  public void testSerializeNullAnnotation() {
    {
      final Map<String, String> m = ImmutableMap.of("a", "a");
      final List<String> l = ImmutableList.of("a");
      assertEquals(
          "{\"s1\":\"a\",\"s2\":\"a\",\"m1\":{\"a\":\"a\"},\"m2\":{\"a\":\"a\"},\"l1\":[\"a\"],\"l2\":[\"a\"]}",
          gson.toJson(new NullAnnotationTestDto("a", "a", m, m, l, l)));
    }
    {
      final Map<String, String> m = new HashMap<>();
      m.put("a", null);
      final List<String> l = new ArrayList<>();
      l.add("a");
      l.add(null);
      assertEquals(
          "{\"s2\":null,\"m1\":{\"a\":null},\"m2\":{\"a\":null},\"l1\":[\"a\",null],\"l2\":[\"a\",null]}",
          gson.toJson(new NullAnnotationTestDto(null, null, m, m, l, l)));
    }
    {
      assertEquals("{\"s2\":null,\"m2\":null,\"l2\":null}",
          gson.toJson(new NullAnnotationTestDto(null, null, null, null, null, null)));
    }
  }

  static class NullAnnotationTestDto {

    public final String s1;
    @GsonSerializeNull
    public final String s2;
    public final Map<String, String> m1;
    @GsonSerializeNull
    public final Map<String, String> m2;
    public final List<String> l1;
    @GsonSerializeNull
    public final List<String> l2;

    NullAnnotationTestDto(String s1, String s2, Map<String, String> m1, Map<String, String> m2,
        List<String> l1, List<String> l2) {
      this.s1 = s1;
      this.s2 = s2;
      this.m1 = m1;
      this.m2 = m2;
      this.l1 = l1;
      this.l2 = l2;
    }

  }

}
