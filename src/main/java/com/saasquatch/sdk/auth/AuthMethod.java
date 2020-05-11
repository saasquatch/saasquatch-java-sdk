package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import com.saasquatch.sdk.annotations.Internal;

/**
 * Method to authenticate with SaaSquatch
 *
 * @author sli
 * @see AuthMethods
 */
public interface AuthMethod {

  @Internal
  void mutateRequest(SimpleHttpRequest request);

  @Internal
  void blockExternalImpl(ExternalImplBlocker externalImplBlocker);

}
