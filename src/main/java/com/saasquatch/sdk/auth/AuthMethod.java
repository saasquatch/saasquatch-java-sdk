package com.saasquatch.sdk.auth;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import com.saasquatch.sdk.annotations.Internal;
import com.saasquatch.sdk.annotations.NoExternalImpl;

/**
 * Method to authenticate with SaaSquatch
 *
 * @author sli
 * @see AuthMethods
 */
@NoExternalImpl
public interface AuthMethod {

  @Internal
  void mutateRequest(SimpleHttpRequest request);

  @Internal
  void blockExternalImpl(ExternalImplBlocker externalImplBlocker);

}
