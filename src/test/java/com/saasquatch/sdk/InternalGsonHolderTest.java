package com.saasquatch.sdk;

import static com.saasquatch.sdk.internal.InternalGsonHolder.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class InternalGsonHolderTest {

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

  static class FooDateDto {

    public final Date foo;

    public FooDateDto(Date foo) {
      this.foo = foo;
    }

  }

}
