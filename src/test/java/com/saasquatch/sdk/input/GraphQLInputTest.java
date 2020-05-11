package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class GraphQLInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> GraphQLInput.newBuilder().build());
  }

}
