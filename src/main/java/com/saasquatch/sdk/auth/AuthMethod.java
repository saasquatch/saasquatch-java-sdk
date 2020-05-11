package com.saasquatch.sdk.auth;

import static com.saasquatch.sdk.internal.InternalUtils.requireNotBlank;
import javax.annotation.Nonnull;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import com.saasquatch.sdk.annotations.Internal;

/**
 * Method to authenticate with SaaSquatch
 *
 * @author sli
 * @see #ofTenantApiKey(String)
 * @see #ofJwt(String)
 */
public abstract class AuthMethod {

  AuthMethod() {}

  @Internal
  public abstract void mutateRequest(SimpleHttpRequest request);

  /**
   * No auth
   */
  public static AuthMethod noAuth() {
    return NoAuth.INSTANCE;
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
