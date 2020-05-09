package com.saasquatch.sdk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker for things that are considered internal and are subject to unnotified change, even though
 * they may be public in Java.
 *
 * @author sli
 */
@Documented
// This annotation is for documentation purposes only, at least for now.
@Retention(RetentionPolicy.SOURCE)
public @interface Internal {

}
