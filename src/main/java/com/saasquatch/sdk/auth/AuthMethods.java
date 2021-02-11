package com.saasquatch.sdk.auth;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import java.util.Objects;
import javax.annotation.Nonnull;
import com.saasquatch.sdk.annotations.Beta;

/**
 * Factory methods for {@link AuthMethod}.
 *
 * @author sli
 */
public final class AuthMethods {

  private AuthMethods() {}

  /**
   * No auth
   */
  public static AuthMethod noAuth() {
    return NoAuth.INSTANCE;
  }

  /**
   * Basic authentication with username and password
   */
  @Beta
  public static AuthMethod ofBasic(@Nonnull String username, @Nonnull String password) {
    return new BasicAuth(Objects.requireNonNull(username, "username"),
        Objects.requireNonNull(password, "password"));
  }

  /**
   * Bearer authentication with a token
   */
  @Beta
  public static AuthMethod ofBearer(@Nonnull String token) {
    return new BearerAuth(requireNotBlank(token, "token"));
  }

  /**
   * Authenticate with the given apiKey
   */
  public static AuthMethod ofTenantApiKey(@Nonnull String apiKey) {
    return new BasicAuth("", requireNotBlank(apiKey, "apiKey"));
  }

  /**
   * Authenticate with the given JWT
   */
  public static AuthMethod ofJwt(@Nonnull String jwt) {
    return new BearerAuth(requireNotBlank(jwt, "jwt"));
  }

}
