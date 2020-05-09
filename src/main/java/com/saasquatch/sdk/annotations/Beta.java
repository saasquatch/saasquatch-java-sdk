package com.saasquatch.sdk.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marker for things that are in beta and are subject to change without warning.
 *
 * @author sli
 */
@Documented
// This annotation is for documentation purposes only, at least for now.
@Retention(RetentionPolicy.SOURCE)
public @interface Beta {

}
