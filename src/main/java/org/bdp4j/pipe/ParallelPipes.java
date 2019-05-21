/*
 * BDP4j implements a pipeline framework to allow definining 
 * project pipelines from XML. The main goal of the pipelines of this 
 * application is to transform imput data received from multiple sources 
 * into fully qualified datasets to be used with Machine Learning.
 *
 * Copyright (C) 2018  Sing Group (University of Vigo)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.bdp4j.pipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.types.PipeType;
import org.bdp4j.util.BooleanBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A class implementing parallel processing of instances
 *
 * @author Yeray Lage
 */
public class ParallelPipes extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(ParallelPipes.class);

    /**
     * The input type
     */
    private Class<?> inputType = null;

    /**
     * The output type
     */
    private Class<?> outputType = null;

    /**
     * Pipes being executed in parallel
     */
    private ArrayList<AbstractPipe> pipes;

    /**
     * Default constructor, initializes the arrayList.
     */
    public ParallelPipes() {
        super(new Class<?>[0], new Class<?>[0]);
        this.pipes = new ArrayList<>();
    }

    /**
     * Constructor that initializes the parallelPipes array and add pipes to it.
     *
     * @param pipeList The array of pipes to be included in the parallelPipe.
     */
    public ParallelPipes(AbstractPipe[] pipeList) {
        super(new Class<?>[0], new Class<?>[0]);
        this.pipes = new ArrayList<>(pipeList.length);

        for (AbstractPipe p : pipeList) {
            this.add(p);
        }
    }

    /**
     * Constructor that initializes the parallelPipes array and add pipes to it.
     *
     * @param pipeList The ArrayList of pipes to be included in the parallelPipe.
     */
    public ParallelPipes(ArrayList<AbstractPipe> pipeList) {
        super(new Class<?>[0], new Class<?>[0]);
        this.pipes = new ArrayList<>(pipeList.size());

        for (AbstractPipe p : pipeList) {
            this.add(p);
        }
    }

    /**
     * Pipe a collection of instances through the whole process.
     *
     * @param carriers Collection of instances to pipe.
     * @return Collection of instances after being processed.
     */
    @Override
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        // Call pipeAll for each pipe included in the parallelPipes
        // Using threads!
        Collection<Instance> clones = new ArrayList<>();
        carriers.forEach((i) -> {
            clones.add(new Instance(i));
        });

        Collection<Instance> ret = pipes.get(0).pipeAll(clones);

        pipes.stream().parallel().forEach((p) -> {
            if (p == null) {
                logger.fatal("AbstractPipe is null");
                System.exit(-1);
            } else {
                if (!p.equals(pipes.get(0))) {
                    Collection<Instance> clones2 = new ArrayList<>();
                    for (Instance i : carriers)
                        clones2.add(new Instance(i));
                    clones2 = p.pipeAll(clones2);

                    // Copy the target if required
                    if (((ArrayList<Instance>) clones2).get(0).getTarget() != null)
                        for (int i = 0; i < clones2.size(); i++) {
                            Serializable target = ((ArrayList<Instance>) clones2).get(i).getTarget();
                            if (target==null) {
                                logger.fatal("Instance with no target: "+((ArrayList<Instance>) clones2).get(i).getName());
                                System.exit(0);
                            }
                            ((ArrayList<Instance>) ret).get(i).setTarget(target);
                        }
                }
            }
        });

        return ret;
    }

    @Override
    public Instance pipe(Instance original) {
        if (pipes.isEmpty()) {
            logger.fatal("[PARALLEL PIPE] ParallelPipe is empty.");
            System.exit(-1);
        }

        Instance originalCopy = new Instance(original); // Copy instance of original for saving Data state.

        // First pipe is the output one, then we use the original one.
        final Instance ret = pipes.get(0).pipe(original);

        // We process the other pipes for getting their properties info.
        pipes.stream().parallel().forEach((p) -> {
            logger.info("PARALLEL PIPE " + p.getClass().getName());

            try {
                if (!p.equals(pipes.get(0))) {
                    // We use the original copy for process with the original data.
                    Instance copy = new Instance(originalCopy); // One copy for each pipe of parallel.

                    if (copy.isValid()) {
                        logger.info("INST " + copy.getName());
                        copy = p.pipe(copy); // Just process pipe for properties set.
                        if (copy.getTarget()!=null){
                            ret.setTarget(copy.getTarget());
                        }
                    } else {
                        logger.info("Skipping invalid instance " + copy.toString());
                    }
                }
            } catch (Exception e) {
                logger.fatal("Exception caught on pipe " + p.getClass().getName() + ". " + e.getMessage()
                        + " while processing instance");
                e.printStackTrace(System.err);
                System.exit(-1);
            }
        });

        // We return the original AbstractPipe, processed the data with the first pipe
        // and the properties with the others.
        return ret;
    }

    /**
     * Adds another pipe.
     *
     * @param pipe The new pipe added.
     */
    public void add(AbstractPipe pipe) {
        System.out.println("pipe " + pipe.toString());
        if (pipes.isEmpty()) {
            // Is pipes arrayList is empty, this is the output AbstractPipe.
            // Then, inputType and outputType of parallelPipes is theirs.
            pipe.setParent(this);
            pipes.add(pipe);
            inputType = pipe.getInputType();
            outputType = pipe.getOutputType();
        } else {
            // In case that pipes arrayList is not empty, this is a property processing
            // pipe.
            // We have to check inputType and match it with actual one.
            if (inputType != pipe.getInputType()) {
                // If inputType doesn't match with actual.
                logger.fatal("[PIPE ADD] Bad compatibility between Pipes: " + pipes.get(0).getClass().getSimpleName()
                        + " | " + pipe.getClass().getSimpleName());
                System.exit(-1);
            }
            pipe.setParent(this);
            pipes.add(pipe);
            logger.info("[PIPE ADD] Good compatibility between Pipes: " + pipes.get(0).getClass().getSimpleName()
                    + " | " + pipe.getClass().getSimpleName());
        }
    }

    /**
     * Determines the input type for the pipe
     *
     * @return the input type (Instance.data) for the pipe
     */
    @Override
    public Class<?> getInputType() {
        return inputType;
    }

    /**
     * Determines the output type (Instance.data) for the pipe
     *
     * @return the output type (Instance.data) for the pipe
     */
    @Override
    public Class<?> getOutputType() {
        return outputType;
    }

    /**
     * Find the position of a pipe
     *
     * @param p The pipe from which we want to get the position
     * @return The position of the pipe
     */
    @Override
    public int findPosition(Pipe p) {
        return this.pipes.indexOf(p);
    }

    /**
     * Return an array of pipes with the current pipe
     *
     * @return a AbstractPipe array containing the pipes that compound the serial
     *         pipes
     */
    public AbstractPipe[] getPipes() {
        if (this.pipes == null) {
            return new AbstractPipe[0];
        }
        AbstractPipe[] returnValue = new AbstractPipe[this.pipes.size()];
        return this.pipes.toArray(returnValue);
    }

    /**
     * Check if alwaysBeforeDeps are satisfied for pipe p. Initially deps contain
     * all alwaysBefore dependences for p. These dependencies are deleted (marked as
     * resolved) by recursivelly calling this method.
     *
     * @param p    The pipe that is being checked
     * @param deps The dependences that are not confirmed in a certain moment
     * @return null if not sure about the fullfulling, true if the dependences are
     *         satisfied, false if the dependences could not been satisfied
     */
    @Override
    public Boolean checkAlwaysBeforeDeps(Pipe p, List<Class<?>> deps) {
        if (!containsPipe(p)) {
            for (AbstractPipe p1 : this.pipes) {
                Boolean retVal = p1.checkAlwaysBeforeDeps(p, deps);
                if (retVal != null) {
                    return retVal;
                }
            }
        } else {
            for (AbstractPipe p1 : this.pipes) {
                if (p1.containsPipe(p)) {
                    Boolean retVal = p1.checkAlwaysBeforeDeps(p, deps);
                    if (retVal != null) {
                        return retVal;
                    } else {
                        return deps.size() == 0; // In this situation deps.size() should no be 0
                    }
                }
            }
        }

        return null;
    }

    /**
     * Check if notBeforeDeps are satisfied for pipe p recursivelly. Note that p
     * should be inserted.
     *
     * @param p The pipe that is being checked
     * @return null if not sure about the fullfulling, true if the dependences are
     *         satisfied, false if the dependences could not been satisfied
     */
    @Override
    public boolean checkNotAfterDeps(Pipe p, BooleanBean foundP) {
        boolean retVal = true;

        if (!containsPipe(p)) {
            for (Pipe p1 : this.pipes) {
                if (p1 instanceof SerialPipes || p1 instanceof ParallelPipes) {
                    retVal = retVal && p1.checkNotAfterDeps(p, foundP);
                } else {
                    if (foundP.getValue()) {
                        retVal = retVal && !(Arrays.asList(p.getNotAfterDeps()).contains(p1.getClass()));
                    }
                    if (!retVal) {
                        errorMessage = "Unsatisfied NotAfter dependency for pipe " + p.getClass().getName() + " ("
                                + p1.getClass().getName() + ")";
                        return retVal;
                    }
                    foundP.Or(p == p1);
                }
            }
        } else {
            AbstractPipe pipeThatContainsP = null;

            int i = 0;
            for (; i < pipes.size() - 1 && !pipes.get(i).containsPipe(p); i++)
                ;
            pipeThatContainsP = pipes.get(i);

            if (pipeThatContainsP != null) { // Should be true
                if (pipeThatContainsP instanceof SerialPipes || pipeThatContainsP instanceof ParallelPipes) {
                    retVal = retVal && pipeThatContainsP.checkNotAfterDeps(p, foundP);
                } else {
                    if (foundP.getValue()) {
                        retVal = retVal && !(Arrays.asList(p.getNotAfterDeps()).contains(pipeThatContainsP.getClass()));
                        if (!retVal) {
                            errorMessage = "Unsatisfied NotAfter dependency for pipe " + p.getClass().getName() + " ("
                                    + pipeThatContainsP.getClass().getName() + ")";
                            return retVal;
                        }
                    }
                    foundP.Or(p == pipeThatContainsP);
                }
            }

            for (AbstractPipe p1 : this.pipes) {
                if (p1 != pipeThatContainsP) {
                    if (p1 instanceof SerialPipes || p1 instanceof ParallelPipes) {
                        retVal = retVal && p1.checkNotAfterDeps(p, foundP);
                    } else {
                        if (foundP.getValue()) {
                            retVal = retVal && !(Arrays.asList(p.getNotAfterDeps()).contains(p1.getClass()));
                            if (!retVal) {
                                errorMessage = "Unsatisfied NotAfter dependency for pipe " + p.getClass().getName()
                                        + " (" + p1.getClass().getName() + ")";
                                return retVal;
                            }
                        }
                        foundP.Or(p == p1);
                    }
                }
            }

        }

        return retVal;
    }

    /**
     * Checks if current pipe contains the pipe p
     *
     * @param p The pipe to search
     * @return true if this pipe contains p false otherwise
     */
    @Override
    public boolean containsPipe(Pipe p) {
        for (Pipe p1 : this.pipes) {
            if (p1.containsPipe(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the dependencies are satisfied
     *
     * @return true if the dependencies are satisfied, false otherwise
     */
    @Override
    public boolean checkDependencies() {
        boolean returnValue = true;

        for (AbstractPipe p1 : pipes) {
            if (!(p1 instanceof SerialPipes) && !(p1 instanceof ParallelPipes)) {
                returnValue = returnValue & getParentRoot().checkAlwaysBeforeDeps(p1,
                        new ArrayList<Class<?>>(Arrays.asList(p1.alwaysBeforeDeps)));
                returnValue = returnValue & getParentRoot().checkNotAfterDeps(p1, new BooleanBean(false));
            } else {
                returnValue = returnValue & p1.checkDependencies();
            }
        }

        return returnValue;
    }

    @Override
    public Integer countPipes(PipeType pipeType) {
        int result = 0;

        for (AbstractPipe p : pipes) {
            result += p.countPipes(pipeType);
        }

        return result;
    }

    /**
     * Achieves a string representation of the piping process
     *
     * @return the string representation of the piping process
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[PP](");

        for (AbstractPipe p : pipes) {
            sb.append(p).append(" | ");
        }

        sb.delete(sb.length() - 3, sb.length());
        sb.append(")");
        return sb.toString();
    }
}
