package com.saasquatch.sdk.auth;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;

import com.saasquatch.sdk.annotations.Beta;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.annotations.NoExternalImpl;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;

/**
 * Method to authenticate with SaaSquatch
 *
 * @author sli
 */
@NoExternalImpl
public interface AuthMethod {

  @Internal
  void mutateRequest(SimpleHttpRequest request);

  /**
   * No auth
   */
  static AuthMethod noAuth() {
    return NoAuth.INSTANCE;
  }

  /**
   * Basic authentication with username and password
   */
  @Beta
  static AuthMethod ofBasic(@Nonnull String username, @Nonnull String password) {
    return new BasicAuth(Objects.requireNonNull(username, "username"),
        Objects.requireNonNull(password, "password"));
  }

  /**
   * Bearer authentication with a token
   */
  @Beta
  static AuthMethod ofBearer(@Nonnull String token) {
    return new BearerAuth(requireNotBlank(token, "token"));
  }

  /**
   * Authenticate with the given apiKey
   */
  static AuthMethod ofTenantApiKey(@Nonnull String apiKey) {
    return new BasicAuth("", requireNotBlank(apiKey, "apiKey"));
  }

  /**
   * Authenticate with the given JWT
   */
  static AuthMethod ofJwt(@Nonnull String jwt) {
    return new BearerAuth(requireNotBlank(jwt, "jwt"));
  }

}
