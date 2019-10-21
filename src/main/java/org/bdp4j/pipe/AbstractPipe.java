/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package org.bdp4j.pipe;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.types.PipeType;
import org.bdp4j.util.BooleanBean;

import java.util.Collection;
import java.util.List;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.PipeUtils;

/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */
/**
 * The abstract superclass of all Pipes, which transform one data type to
 * another. Pipes are most often used for feature extraction.
 * <p>
 * A pipe operates on an {@link org.bdp4j.types.Instance}, which is a carrier of
 * data. A pipe reads from and writes to fields in the Instance when it is
 * requested to process the instance. It is up to the pipe which fields in the
 * Instance it reads from and writes to, but usually a pipe will read its input
 * from and write its output to the "data" field of an instance.
 * <p>
 * Pipes can be hierachically composed. In a typical usage, a SerialPipe is
 * created which holds instances of other pipes in an ordered list. Piping in
 * instance through a SerialPipe means piping the instance through the child
 * pipes in sequence.
 *
 * @author Andrew McCallum
 * <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 * @author Jose Ramon Mendez
 * @author Maria Novo
 * @author Yeray Lage
 */
public abstract class AbstractPipe implements Pipe {

    private static final Logger logger = LogManager.getLogger(AbstractPipe.class);
    /**
     * Error message for dependencies
     */
    static String errorMessage;
    /**
     * Dependencies of the type alwaysBefore These dependences indicate what
     * pipes must be executed before the current one.
     */
    final Class<?>[] alwaysBeforeDeps;
    /**
     * Dependencies of the type notAfter These dependences indicate what pipes
     * must not be executed after the current one.
     */
    final Class<?>[] notAfterDeps;
    /**
     * Marks if the next instance to pipe will be the last one to pipe
     */
    boolean isLast = false;
    /**
     * The parent pipe for this one
     */
    AbstractPipe parent;

    /**
     * For debugging purposes.
     */
    boolean debugging = false;

    /**
     * Create a pipe with its dependences
     *
     * @param alwaysBeforeDeps The dependences alwaysBefore (pipes that must be
     * executed before this one)
     * @param notAfterDeps The dependences notAfter (pipes that cannot be
     * executed after this one)
     */
    public AbstractPipe(Class<?>[] alwaysBeforeDeps, Class<?>[] notAfterDeps) {
        this.notAfterDeps = notAfterDeps;
        this.alwaysBeforeDeps = alwaysBeforeDeps;
    }

    /**
     * Get the error Message dependencies
     *
     * @return The error message
     */
    public static String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     * <p>
     * One can create a new concrete subclass of AbstractPipe simply by
     * implementing this method.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    public abstract Instance pipe(Instance carrier);

    /**
     * AbstractPipe all instances from a Collection
     *
     * @param carriers Collection of instances to pipe
     * @return The collection of instances after being processed
     */
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        Instance[] carriersAsArray = carriers.toArray(new Instance[0]);

        //Search the last valid instance
        int lastValidInstanceIdx = carriers.size() - 1;
        while (!carriersAsArray[lastValidInstanceIdx].isValid() && lastValidInstanceIdx > 0) {
            lastValidInstanceIdx--;
        }

        if (lastValidInstanceIdx == 0 && !carriersAsArray[lastValidInstanceIdx].isValid()) {
            logger.fatal("All instances were invalidated.");
            return carriers;
        }

        try {
            //Pipe all instances except the last one
            isLast = false;

            // This is the thread-way
            /* Arrays.stream(carriersAsArray).parallel().forEach(
                    (c) -> {
                        if (c.isValid()) {
                            pipe(c);
                        } else {
                            logger.info("Skipping invalid instance " + c.toString());
                        }
                    }
            );
             */
            // This is the serial-way
            for (int i = 0; i < lastValidInstanceIdx; i++) {
                if (carriersAsArray[i].isValid()) {
                    int numberOfPropertiesBefore = carriersAsArray[i].getPropertyList().size();
                    int numberOfPropertiesAfter = 0;
                    boolean propertyComputingPipe = (getClass().getAnnotation(PropertyComputingPipe.class) != null);

                    pipe(carriersAsArray[i]);
                    if (propertyComputingPipe) {
                        numberOfPropertiesAfter = carriersAsArray[i].getPropertyList().size();

                        if (numberOfPropertiesBefore >= numberOfPropertiesAfter) {
                            logger.fatal("[PIPE ALL] Error adding properties in " + this.getClass().getSimpleName());
                            Configurator.setIrrecoverableErrorInfo("[PIPE ALL] Error adding properties in " + this.getClass().getSimpleName());
                            Configurator.getActionOnIrrecoverableError().run();
                        }
                    }
                } else {
                    logger.info("Skipping invalid instance " + carriersAsArray[i].toString());
                }
            }

            //AbstractPipe the last valid instance
            isLast = true;
            pipe(carriersAsArray[lastValidInstanceIdx]);
        } catch (Exception e) {
            logger.fatal("Exception caught on pipe " + getClass().getName() + ". " + e.getMessage() + " while processing instance");
            e.printStackTrace(System.err);
            Configurator.setIrrecoverableErrorInfo("Exception caught on pipe " + getClass().getName() + ". " + e.getMessage() + " while processing instance");
            Configurator.getActionOnIrrecoverableError().run();
        }

        return carriers;
    }

    /**
     * Finds the parent AbstractPipe
     *
     * @return the parent AbstractPipe
     */
    public AbstractPipe getParent() {
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

        parent = (AbstractPipe) p;
    }

    /**
     * Stablished mode debug to pipe
     *
     * @param debugging True if you want to debug this pipe, false otherwise
     */
    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }

    /**
     * Get if pipe is marked to debug or not
     *
     * @return True if pipe is marked to debug, false otherwise
     */
    public boolean isDebugging() {
        return this.debugging;
    }

    /**
     * Check if current pipe has a brother(pipe at the same level) marked to
     * debug
     *
     * @return True is current pipe has a brother marked to debug.
     */
    private boolean hasBrotherDebuggingPipe() {
        int position = 0;
        AbstractPipe parentPipe = this.getParent();
        if (parentPipe.isDebugging()) {
            return true;
        }
        if (parentPipe instanceof SerialPipes) {
            position = this.getParent().findPosition(this);
            for (int i = 0; i < position; i++) {
                if (((SerialPipes) parentPipe).getPipe(i).isDebugging()) {
                    return true;
                }
            }
        } else if (parentPipe instanceof ParallelPipes) {
            position = this.getParent().findPosition(this);
            for (int i = 0; i <= position; i++) {
                if (((ParallelPipes) parent).getPipes()[i].isDebugging()) {
                    return true;
                }
            }
        } else {
            if (parent.isDebugging()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if current pipe is marked to debug
     *
     * @return True is current pipe is marked to debug.
     */
    public boolean isDebuggingPipe() {
        if (this.isDebugging()) {
            if (this.getParent() != null) {
                this.getParent().setDebugging(true);
            }
            return true;
        } else if (this.getParent() == null) {
            return false;
        } else if (hasBrotherDebuggingPipe()) {
            return true;
        } else {
            return this.getParent().isDebuggingPipe();
        }
    }

    /**
     * Achieves a string representation of the piping process
     *
     * @return the String representation of the pipe
     */
    /**
     * Finds the parent root
     *
     * @return the root parent
     */
    @Override
    public AbstractPipe getParentRoot() {
        if (parent == null) {
            return this;
        }

        AbstractPipe p = parent;

        while (p.parent != null) {
            p = p.parent;
        }

        return p;
    }

    /**
     * Get the dependences alwaysBefore
     *
     * @return the dependences alwaysBefore
     */
    @Override
    public Class<?>[] getAlwaysBeforeDeps() {
        return this.alwaysBeforeDeps;
    }

    /**
     * Get the dependences notAfter
     *
     * @return the dependences notAfter
     */
    @Override
    public Class<?>[] getNotAfterDeps() {
        return this.notAfterDeps;
    }

    /**
     * Say whether the current Instance is the last being processed
     *
     * @return true if the current Instance is the last being processed
     */
    @Override
    public boolean isLast() {
        return isLast;
    }

    /**
     * Return the output type included the data attribute of a Instance
     *
     * @return the output type for the data attribute of the Instances processed
     */
    @Override
    public abstract Class<?> getInputType();

    /**
     * Say datatype expected in the data attribute of a Instance
     *
     * @return the datatype expected in the data attribute of a Instance
     */
    @Override
    public abstract Class<?> getOutputType();

    /**
     * Find the position of a pipe
     *
     * @param p The pipe from which we want to get the position
     * @return The position of the pipe
     */
    public int findPosition(Pipe p) {
        if (p.getParent() instanceof SerialPipes) {
            return ((SerialPipes) p.getParent()).findPosition(p);
        } else if (p.getParent() instanceof ParallelPipes) {
            return ((ParallelPipes) p.getParent()).findPosition(p);
        }
        return 0;
    }

    /**
     * Check if a path exists, and otherwise, creates that.
     *
     * @param path that needs to create
     * @return the path created, empty string otherwise
     */
    public String getPath(String path) {
        File filepath = new File(path);
        if (filepath.exists()) {
            return path;
        } else if (filepath.mkdirs()) {
            return path;
        }
        return "";
    }

    /**
     * Get the store path to save data
     *
     * @param carriers The collection of carriers to be stored
     * @return the store path to save data
     */
    @Override
    public String getStorePath(Collection<Instance> carriers) {
        String storePath = "";
        String fileSeparator = System.getProperty("file.separator");
        Configurator configurator = Configurator.getLastUsed();
        String temp_folder = configurator.getProp(Configurator.TEMP_FOLDER);

        File sourcePath = new File(temp_folder);
        if (!sourcePath.exists()) {
            sourcePath.mkdir();
        }
        if (getParent() == null) {
            if (this instanceof SerialPipes || this instanceof ParallelPipes) {
                storePath = temp_folder + PipeUtils.generateMD5(this.toString() + carriers) + fileSeparator;
                return getPath(storePath);
            } else {
                return findPosition(this) + "_" + PipeUtils.generateMD5(this.toString()) + ".ser";
            }
        } else {
            if (this instanceof SerialPipes || this instanceof ParallelPipes) {
                storePath = getParent().getStorePath(carriers) + this.getParent().findPosition(this) + "_" + PipeUtils.generateMD5(this.toString()) + fileSeparator;
                return getPath(storePath);

            } else {
                return getParent().getStorePath(carriers) + findPosition(this) + "_" + PipeUtils.generateMD5(this.toString()) + ".ser";
            }
        }
    }

    /**
     * Check if alwaysBeforeDeps are satisfied for pipe p (inserted). Initially
     * deps contain all alwaysBefore dependences for p. These dependencies are
     * deleted (marked as resolved) by recursivelly calling this method.
     *
     * @param p The pipe that is being checked
     * @param deps The dependences that are not confirmed in a certain moment
     * @return null if not sure about the fullfulling, true if the dependences
     * are satisfied, false if the dependences could not been satisfied
     */
    @Override
    public Boolean checkAlwaysBeforeDeps(Pipe p, List<Class<?>> deps) {
        if (this == (AbstractPipe) p && deps.size() > 0) {
            errorMessage = "Unsatisfied AlwaysBefore dependencies for pipe " + ((AbstractPipe) p).getClass().getName() + " (";
            boolean first = true;
            for (Class<?> dep : deps) {
                errorMessage += ((!first ? ", " : "") + dep.getName());
                first = false;
            }
            errorMessage += ")";

            return false;
        }

        deps.remove(this.getClass());

        if (deps.isEmpty()) {
            return true;
        }

        return null;
    }

    /**
     * Check if notBeforeDeps are satisfied for pipe p recursivelly. Note that p
     * should be inserted.
     *
     * @param p The pipe that is being checked
     * @param foundP Used for intenal representation. On calling use always new BooleanBean(false);
     * @return null if not sure about the fullfulling, true if the dependences
     * are satisfied, false if the dependences could not been satisfied
     */
    @Override
    public boolean checkNotAfterDeps(Pipe p, BooleanBean foundP) {
        if (this == (AbstractPipe) p) {
            return true;
        } else {
            throw new RuntimeException("Seems this situation has no sense.");
        }
    }

    /**
     * Checks if current pipe contains the pipe p
     *
     * @param p The pipe to search
     * @return true if this pipe contains p false otherwise
     */
    @Override
    public boolean containsPipe(Pipe p) {
        return this == (AbstractPipe) p;
    }

    /**
     * Checks if the dependencies are satisfied
     *
     * @return true if the dependencies are satisfied, false otherwise
     */
    @Override
    public boolean checkDependencies() {
        return this.alwaysBeforeDeps.length == 0;
    }

    /**
     * Count the number of pipes included in this pipe of a certain type
     * @param c The type of pipes to count
     * @return The number of found pipes
     */
    @Override
    public Integer countPipes(PipeType c) {
        return (this.getClass().getAnnotationsByType((Class)(c.typeClass())).length != 0) ? 1 : 0;
    }

    /**
     * Achieves a string representation of the piping process
     *
     * @return the String representation of the pipe
     */
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
