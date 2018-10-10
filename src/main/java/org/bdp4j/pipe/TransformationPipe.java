package org.bdp4j.pipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

/**
 * This annotation is used to indicate which pipes able to transform 
 * the data of an instance (maybe replacing the object included in the 
 * data field). 
 * @author Yeray Lage
 */
public @interface TransformationPipe {
}
