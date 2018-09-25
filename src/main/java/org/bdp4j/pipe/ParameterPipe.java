package org.bdp4j.pipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/**
 * This annotation is used to indicate which pipes have required parameters, in order to use from an UI.
 */
public @interface ParameterPipe {
    String name();
    String description();
}