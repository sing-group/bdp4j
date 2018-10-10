package org.bdp4j.pipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

/**
 * This annotation is used to indicate which pipes add properties to Instances.
 * The property could be a date or the language in which a text is written.
 * Properties are saved to the Instance and the data is not modified.
 * @author Yeray Lage
 */
public @interface PropertyComputingPipe {
}