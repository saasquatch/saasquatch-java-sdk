package com.saasquatch.sdk.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class GraphQLInputTest {

  @Test
  public void testValidation() {
    assertThrows(NullPointerException.class, () -> GraphQLInput.newBuilder().build());
  }

  @Test
  public void testBasic() {
    assertEquals("a", GraphQLInput.ofQuery("a").getQuery());
    final GraphQLInput graphQLInput = GraphQLInput.newBuilder().setQuery("a").setOperationName("b")
        .setVariables(Collections.emptyMap()).build();
    assertEquals("a", graphQLInput.getQuery());
    assertEquals("b", graphQLInput.getOperationName());
    assertEquals(Collections.emptyMap(), graphQLInput.getVariables());
  }

}
