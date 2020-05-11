package com.saasquatch.sdk.internal.json;

import com.saasquatch.sdk.annotations.Internal;

/**
 * For things that have special JSON serialization logic instead of using the global Gson.
 *
 * @author sli
 */
@Internal
public interface HasCustomJsonSerialization {

  String toJsonString();

}
