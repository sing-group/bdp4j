package org.bdp4j.pipe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)

/**
 * This annotation is used to indicate which pipes have required parameters, in order to use from an UI.
 * @author María Novo
 * @author Yeray Lage
 */
public @interface PipeParameter {
	/**
	  * The name for the parameter
	  * @return the name of the parameter
	  */
    String name();
	
	/**
	  * The description for the parameter
	  * @return a description of the parameter
	  */
    String description();
	
	/**
	  * The default value for the parameter
	  * @return the default value for the parameter
	  */
	 String defaultValue();
}