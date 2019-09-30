package com.saasquatch.sdk;

import static com.saasquatch.sdk.InternalUtils.requireNotBlank;
import static java.nio.charset.StandardCharsets.UTF_8;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import okhttp3.Request;

/**
 * Method to authenticate with SaaSquatch
 *
 * @author sli
 * @see #ofApiKey(String)
 * @see #ofJwt(String)
 */
@Immutable
public abstract class AuthMethod {

  private final boolean canBeClientDefault;

  private AuthMethod(boolean canBeClientDefault) {
    this.canBeClientDefault = canBeClientDefault;
  }

  protected abstract void mutateRequest(Request.Builder requestBuilder);

  final boolean canBeClientDefault() {
    return canBeClientDefault;
  }

  /**
   * No auth
   */
  public static AuthMethod noAuth() {
    return NoAuth.INSTANCE;
  }

  /**
   * Authenticate with the given apiKey
   */
  public static AuthMethod ofApiKey(@Nonnull String apiKey) {
    return new ApiKeyAuth(requireNotBlank(apiKey, "apiKey"));
  }

  /**
   * Authenticate with the given JWT
   */
  public static JwtAuth ofJwt(@Nonnull String jwt) {
    return new JwtAuth(requireNotBlank(jwt, "jwt"));
  }

  private static class NoAuth extends AuthMethod {

    static final NoAuth INSTANCE = new NoAuth();

    public NoAuth() {
      super(true);
    }

    @Override
    protected void mutateRequest(Request.Builder requestBuilder) {}

  }

  private static class ApiKeyAuth extends AuthMethod {

    private final String apiKey;

    ApiKeyAuth(String apiKey) {
      super(true);
      this.apiKey = apiKey;
    }

    @Override
    protected void mutateRequest(Request.Builder requestBuilder) {
      requestBuilder.header("Authorization",
          "Basic " + OkioBase64.encode((":" + apiKey).getBytes(UTF_8)));
    }

  }

  private static class JwtAuth extends AuthMethod {

    private final String jwt;

    JwtAuth(String jwt) {
      super(false);
      this.jwt = jwt;
    }

    @Override
    protected void mutateRequest(Request.Builder requestBuilder) {
      requestBuilder.header("Authorization", "Bearer " + jwt);
    }

  }

}
