package org.bdp4j.pipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

/**
 * This annotation is used to indicate which pipes are able to store instances
 * to disk (maybe in CSV or ARFF formats). They acts in a similar form to 
 * the tee command in Unix Systems.
 * @author Yeray Lage
 */
public @interface TeePipe {
}