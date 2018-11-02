/* Copyright (C) 2002 Univ. of Vigo, SING Group
   This file has been modified from the original one belonging to "MALLET"
   (MAchine Learning for LanguagE Toolkit) project. Consequently this file
   and the rest of the project is publised under the Common Plublic License, 
   version 1.0, as published by http://www.opensource.org. For further information
   see, seee the file 'LICENSE' included in this distribution. */
 /* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */
package org.bdp4j.pipe;

import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bdp4j.ia.types.Instance;

/**
 * The abstract superclass of all Pipes, which transform one data type to
 * another. Pipes are most often used for feature extraction.
 * <p>
 * A pipe operates on an {@link org.bdp4j.ia.types.Instance}, which is a carrier
 * of data. A pipe reads from and writes to fields in the Instance when it is
 * requested to process the instance. It is up to the pipe which fields in the
 * Instance it reads from and writes to, but usually a pipe will read its input
 * from and write its output to the "data" field of an instance.
 * <p>
 * Pipes can be hierachically composed. In a typical usage, a SerialPipe is
 * created which holds instances of other pipes in an ordered list. Piping in
 * instance through a SerialPipe means piping the instance through the child
 * pipes in sequence.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 * @author Jose Ramon Mendez
 * @author Maria Novo
 * @author Yeray Lage
 */
public abstract class Pipe {
    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Pipe.class);
	
    /**
     * Marks if the next instance to pipe will be the last one to pipe
     */
    boolean isLast = false;

    /**
     * The parent pipe for this one
     */
    Pipe parent;

    /**
     * Construct a pipe with no data and target dictionaries
     */
    public Pipe() {
    }

    /**
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     * <p>
     * One can create a new concrete subclass of Pipe simply by implementing
     * this method.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    public abstract Instance pipe(Instance carrier);

    /**
     * Pipe all instances from a Collection
     *
     * @param carriers Collection of instances to pipe
     * @return The collection of instances after being processed
     */
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        Instance[] carriersAsArray = carriers.toArray(new Instance[0]);

		  //Search the last valid instance
		  int lastValidInstanceIdx=carriers.size()-1;
        while (!carriersAsArray[lastValidInstanceIdx].isValid() ){
           lastValidInstanceIdx--;
        }
		  
        try {		  
		     //Pipe all instances except the last one
			  isLast=false;
           for (int i=0;i<lastValidInstanceIdx;i++){
              if (carriersAsArray[i].isValid()) {
                  pipe(carriersAsArray[i]);
              } else {
                  logger.info("Skipping invalid instance " + carriersAsArray[i].toString());
              }
           }
		  
		     //Pipe the last valid instance
           isLast=true;
           pipe(carriersAsArray[lastValidInstanceIdx]);
        } catch (Exception e) {
           logger.fatal("Exception caught on pipe " + getClass().getName() + ". " + e.getMessage() + " while processing instance");
           e.printStackTrace(System.err);
           System.exit(0);
        }
		  
        return carriers;
    }

    /**
     * Finds the parent Pipe
     *
     * @return the parent Pipe
     */
    public Pipe getParent() {
        return parent;
    }

    /**
     * Stablished the parent pipe for this one
     *
     * @param p The parent Pipe for this one
     */
    public void setParent(Pipe p) {
        if (parent != null) {
            throw new IllegalStateException("Parent already set.");
        }

        parent = p;
    }

    /**
     * Finds the parent root
     *
     * @return the root parent
     */
    public Pipe getParentRoot() {
        if (parent == null) {
            return this;
        }

        Pipe p = parent;

        while (p.parent != null) {
            p = p.parent;
        }

        return p;
    }

    /**
     * Say whether the current Instance is the last being processed
     *
     * @return true if the current Instance is the last being processed
     */
    public boolean isLast() {
        return isLast;
    }

    /**
     * Return the output type included the data attribute of a Instance
     * @return the output type for the data attribute of the Instances processed
     */
    public abstract Class<?> getInputType();

    /**
     * Say datatype expected in the data attribute of a Instance
     * @return the datatype expected in the data attribute of a Instance
     */
    public abstract Class<?> getOutputType();
}
