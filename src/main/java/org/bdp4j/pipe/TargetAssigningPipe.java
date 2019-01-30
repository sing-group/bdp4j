package org.bdp4j.pipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)

/**
 *This annotation is used to indicate which pipesassign a target to an instance.
 * @author Yeray Lage
 */
public @interface TargetAssigningPipe {

}