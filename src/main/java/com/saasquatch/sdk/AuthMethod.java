package com.saasquatch.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.util.Objects;
import javax.annotation.Nonnull;
import okhttp3.Credentials;
import okhttp3.Request;
import okhttp3.Request.Builder;

/**
 * Method to authenticate with SaaSquatch
 *
 * @author sli
 */
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
    return new ApiKeyAuth(Objects.requireNonNull(apiKey, "apiKey"));
  }

  /**
   * Authenticate with the given JWT
   */
  public static JwtAuth ofJwt(@Nonnull String jwt) {
    return new JwtAuth(Objects.requireNonNull(jwt, "jwt"));
  }

  static class NoAuth extends AuthMethod {

    static final NoAuth INSTANCE = new NoAuth();

    public NoAuth() {
      super(true);
    }

    @Override
    protected void mutateRequest(Builder requestBuilder) {}

  }

  static class ApiKeyAuth extends AuthMethod {

    private final String apiKey;

    ApiKeyAuth(String apiKey) {
      super(true);
      this.apiKey = apiKey;
    }

    @Override
    protected void mutateRequest(Request.Builder requestBuilder) {
      requestBuilder.header("Authorization", Credentials.basic("", apiKey, UTF_8));
    }

  }

  static class JwtAuth extends AuthMethod {

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
