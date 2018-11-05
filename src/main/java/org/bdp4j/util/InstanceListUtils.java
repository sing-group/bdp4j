package org.bdp4j.util;

import java.util.*;

import org.bdp4j.types.Instance;


/**
 * Implements distinct utilities to process Instances
 */
public class InstanceListUtils {
	
	/**
	  * Drop invalid instances from a list of Instances
	  * @param l List of Instances
	  * @return List of instances whitout invalid instances
	  */
    public static List<Instance> dropInvalid(List<Instance> l){
	    Iterator<Instance> listIt=l.iterator();
	    while (listIt.hasNext()){
		    Instance i=listIt.next();
		    if (!i.isValid()) listIt.remove();
	    }
  	    return l;
    }

}