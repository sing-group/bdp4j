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
 */


public class SerialPipes extends Pipe {
    private Class<?> inputType = null;
    private Class<?> outputType = null;

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
     * Pipes
     */
    private ArrayList<Pipe> pipes;

    public SerialPipes() {
        this.pipes = new ArrayList<Pipe>();
    }

    public SerialPipes(Object[] pipes) {
        this((Pipe[]) pipes);
    }

    public SerialPipes(Pipe[] pipes) {
        this.pipes = new ArrayList<Pipe>(pipes.length);

        //System.out.println ("SerialPipes init this = "+this);
        for (Pipe pipe : pipes) this.add(pipe);
    }


    public SerialPipes(ArrayList<Pipe> pipeList //added by Fuchun
    ) {
        this.pipes = new ArrayList<Pipe>(pipeList.size());

        for (Pipe aPipeList : pipeList) {
            this.add(aPipeList);
        }
    }

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
                logger.error("[SERIAL PIPE ADD] BAD compatibility between Pipes.");
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

    private boolean checkCompatibility(Pipe pipe) {
        // Last pipe con ArrayList
        Pipe lastPipe = pipes.get(pipes.size() - 1);

        System.out.println("Last pipe - " + lastPipe.getInputType() + " | " + lastPipe.getOutputType());
        System.out.println("New pipe - " + pipe.getInputType() + " | " + pipe.getOutputType());

        return lastPipe.getOutputType() == pipe.getInputType();
    }

    public Instance pipe(Instance carrier, int startingIndex) {
        carrier = getInstance(carrier, startingIndex);

        return carrier;
    }

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

    // Call this version when you are not training and don't want conjunctions to mess up the decoding.
    public Instance pipe(Instance carrier, int startingIndex,
                         boolean growAlphabet) {
        System.out.print("*");
        carrier = getInstance(carrier, startingIndex);
        return carrier;
    }

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
                            System.out.println("INST " + carrier.getName());
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

    public void removePipe(int index) {
        try {
            pipes.remove(index);
        } catch (Exception e) {
            System.err.println(
                    "Error removing pipe. Index = " + index + ".  " +
                            e.getMessage());
        }
    }

    //added by Fuchun Jan.30, 2004
    public void replacePipe(int index, Pipe p) {
        try {
            pipes.set(index, p);
        } catch (Exception e) {
            System.err.println(
                    "Error replacing pipe. Index = " + index + ".  " +
                            e.getMessage());
        }
    }

    public int size() {
        return pipes.size();
    }

    public Pipe getPipe(int index) {
        Pipe retPipe = null;

        try {
            retPipe = pipes.get(index);
        } catch (Exception e) {
            System.err.println(
                    "Error getting pipe. Index = " + index + ".  " +
                            e.getMessage());
        }

        return retPipe;
    }

    public Instance pipe(Instance carrier) {
        return pipe(carrier, 0);
    }

    
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Pipe pipe : pipes) sb.append(pipe.toString()).append(",");

        return sb.toString();
    }

}
