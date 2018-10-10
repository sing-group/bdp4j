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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;

/**
 * Convert an instance through a sequence of pipes.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 * @author Yeray Lage
 * @author Maria Novo Loures
 * @author José Ramón Méndez
 */
public class SerialPipes extends Pipe {
    private Class<?> inputType = null;
    private Class<?> outputType = null;

    /**
		* For logging purposes
		*/
    private static final Logger logger = LogManager.getLogger(SerialPipes.class);

    @Override
    public Class<?> getInputType() {
        return inputType;
    }

    @Override
    public Class<?> getOutputType() {
        return outputType;
    }

    /**
     * Pipe list
     */
    private ArrayList<Pipe> pipes;

    /**
		* Build an empty SerialPipes
		*/
    public SerialPipes() {
        this.pipes = new ArrayList<Pipe>();
    }

    /**
		* Build a serial pipes using an array of individual pipes
		* @param pipes An array of pipes that will be included in the serialPipes in the same order
		*/
    public SerialPipes(Pipe[] pipes) {
        this.pipes = new ArrayList<Pipe>(pipes.length);

        for (Pipe pipe : pipes) this.add(pipe);
    }

    /**
		* Build a serial pipes using an ArrayList of individual pipes
		* @param pipeList An arrayList of pipes that will be included in the serialPipes in the same order
		*/
    public SerialPipes(ArrayList<Pipe> pipeList) {
        this.pipes = new ArrayList<Pipe>(pipeList.size());

        for (Pipe aPipeList : pipeList) {
            this.add(aPipeList);
        }
    }

    /**
		* Return an array of pipes with the current pipe
		* @return a Pipe array containing the pipes that compound the serial pipes
		*/
    public Pipe[] getPipes() {
        if (pipes == null) return new Pipe[0];
        Pipe[] returnValue = new Pipe[pipes.size()];
        return pipes.toArray(returnValue);
    }

    /**
     * Set the pipes used
     *
     * @param pipes pipes used
     */

    public void setPipes(Pipe[] pipes) {
        this.pipes = new ArrayList<Pipe>(pipes.length);

        //System.out.println ("SerialPipes init this = "+this);
        for (Pipe pipe : pipes) this.add(pipe);
    }

    /**
     * Returns the current Pipe
     *
     * @return current Pipe
     */
    public SerialPipes getPipe() {
        return this;
    }

    /**
		* Add a new pipe at the end of the processing list
		* @param pipe The new pipe to be added 
		*/
    public void add(Pipe pipe) {
        if (!pipes.isEmpty()) {
            if (checkCompatibility(pipe)) {
                logger.info("[PIPE ADD] Good compatibility between Pipes.");
                pipe.setParent(this);
                pipes.add(pipe);

                if (inputType == null) {
                    // If first Pipe hasn't inputType
                    inputType = pipe.getInputType();
                }

                outputType = pipe.getInputType();
            } else {
                logger.fatal("[SERIAL PIPE ADD] BAD compatibility between Pipes.");
                System.exit(0);
            }
        } else {
            // If first Pipe
            pipe.setParent(this);
            pipes.add(pipe);

            inputType = pipe.getInputType();
            outputType = pipe.getOutputType();
        }
    }

    /**
		* Checks the compatibility of a pipe that will be added at the end of the pipelist
		* @param pipe The pipe that will be added
		* @return true if the pipe can be placed at the end of the pipelist
		*/
    private boolean checkCompatibility(Pipe pipe) {
        Pipe lastPipe = pipes.get(pipes.size() - 1);

        return lastPipe.getOutputType() == pipe.getInputType();
    }

    /**
		* Pipe an instance starting in a certain position of the pipe
		* @param carrier The instance to be processed
		* @param startingIndex The position of the fisrt pipe 
		* @return The instance achieved as result of processing
		*/
    public Instance pipe(Instance carrier, int startingIndex) {
        carrier = getInstance(carrier, startingIndex);

        return carrier;
    }

    /**
		* Computes the instance transformation when executing the pipe processing from a certain execution point
		* @param carrier The instance to be pipes
		* @param startingIndex The execution point to begin the processing
		* @return the instance after being processed
		*/
    private Instance getInstance(Instance carrier, int startingIndex) {
        for (int i = startingIndex; i < pipes.size(); i++) {

            Pipe p = pipes.get(i);

            if (p == null) {
                logger.fatal("Pipe " + i + " is null");
                System.exit(0);
            } else {

                try {
                    if (carrier.isValid()) {
                        carrier = p.pipe(carrier);
                    } else {
                        logger.info("Skipping invalid instance " + carrier.toString());
                    }
                } catch (Exception e) {
                    logger.fatal("Exception caught on pipe " + i + " (" + p.getClass().getName() + "). " + e.getMessage() + " while processing " + carrier.toString());
                    e.printStackTrace(System.err);
                    System.exit(0);
                }
            }
        }
        return carrier;
    }

	 /**
		* Pipe a collection of instances through the whole process
		* @param carriers The instances to be processed
		* @return the instances after processing them 
		*/
    @Override
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        for (int i = 0; i < pipes.size(); i++) {
            Pipe p = pipes.get(i);
            if (p == null) {
                logger.fatal("Pipe " + i + " is null");
                System.exit(0);
            } else {
                try {
                    Iterator<Instance> it=carriers.iterator();
                    while (it.hasNext()) {
                        Instance carrier=it.next();
                        if (carrier.isValid()) {
                            //System.out.println("INST " + carrier.getName());
                            carrier = p.pipe(carrier);
                        }else{
                             logger.info("Skipping invalid instance " + carrier.toString());
                        }
                        isLast=!it.hasNext();
                    }
                 } catch (Exception e) {
                    logger.fatal("Exception caught on pipe " + i + " (" + p.getClass().getName() + "). " + e.getMessage() + " while processing instance");
                    e.printStackTrace(System.err);
                    System.exit(0);
                }
            }
        }
        return carriers;
    }

    /**
		* Remove a pipe from the processing pipe
		* @param index The position of pipe that will be removed
		*/
    public void removePipe(int index) {
        try {
            pipes.remove(index);
        } catch (Exception e) {
            logger.error("Error removing pipe. Index = " + index + ".  " +  e.getMessage());
        }
    }

    /**
		* Replace a pipe in the pipeList
		* @param index The position of the pipe that will be replaced
		* @param p The new Pipe
		*/
    //added by Fuchun Jan.30, 2004
    public void replacePipe(int index, Pipe p) {
        try {
            pipes.set(index, p);
        } catch (Exception e) {
            logger.error("Error replacing pipe. Index = " + index + ".  " + e.getMessage());
        }
    }

    /**
		* Computes the number of pipes included in the SerialPipes
		* @return the number of pipes includen in current SerialPipes
		*/
    public int size() {
        return pipes.size();
    }

    /**
		* Returns the pipe included in a position
		* @param index The position of the pipe
		* @return the pipe included in the desired position
		*/
    public Pipe getPipe(int index) {
        Pipe retPipe = null;

        try {
            retPipe = pipes.get(index);
        } catch (Exception e) {
            logger.error("Error getting pipe. Index = " + index + ".  " + e.getMessage());
        }

        return retPipe;
    }

    /**
		* Pipe a instance through the whole piping process
		* @param carrier the instance to be piped
		* @return The instance achieved as processing result
		*/
    public Instance pipe(Instance carrier) {
        return pipe(carrier, 0);
    }

	 /**
		* Achieves a string representation of the piping process
		* @return the string representation of the piping process
		*/
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Pipe pipe : pipes) sb.append(pipe.toString()).append(",");

        return sb.toString();
    }

}
