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

import org.bdp4j.types.Instance;
import org.bdp4j.types.PipeType;
import org.bdp4j.util.BooleanBean;

import java.util.Collection;
import java.util.List;

public interface Pipe {

    public Instance pipe(Instance carrier);

    public Collection<Instance> pipeAll(Collection<Instance> carriers);

    /**
     * Finds the parent Pipe
     *
     * @return the parent Pipe
     */
    public Pipe getParent();

    /**
     * Stablished the parent pipe for this one
     *
     * @param p The parent Pipe for this one
     */
    void setParent(Pipe p);

    /**
     * Finds the parent root
     *
     * @return the root parent
     */
    public Pipe getParentRoot();

    /**
     * Get the dependences alwaysBefore
     *
     * @return the dependences alwaysBefore
     */
    public Class<?>[] getAlwaysBeforeDeps();

    /**
     * Get the dependences notAfter
     *
     * @return the dependences notAfter
     */
    public Class<?>[] getNotAfterDeps();

    /**
     * Say whether the current Instance is the last being processed
     *
     * @return true if the current Instance is the last being processed
     */
    boolean isLast();

    /**
     * Return the output type included the data attribute of a Instance
     *
     * @return the output type for the data attribute of the Instances processed
     */
    public Class<?> getInputType();

    /**
     * Say datatype expected in the data attribute of a Instance
     *
     * @return the datatype expected in the data attribute of a Instance
     */
    public Class<?> getOutputType();

    /**
     * Get the store path to save data
     *
     * @return the store path to save data
     */
    String getStorePath(Collection<Instance> carriers);

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
    Boolean checkAlwaysBeforeDeps(Pipe p, List<Class<?>> deps);

    /**
     * Check if notBeforeDeps are satisfied for pipe p recursivelly. Note that p
     * should be inserted.
     *
     * @param p The pipe that is being checked
     * @param foundP // TODO what is this for?
     * @return null if not sure about the fullfulling, true if the dependences
     * are satisfied, false if the dependences could not been satisfied
     */
    boolean checkNotAfterDeps(Pipe p, BooleanBean foundP);

    /**
     * Checks if current pipe contains the pipe p
     *
     * @param p The pipe to search
     * @return true if this pipe contains p false otherwise
     */
    public boolean containsPipe(Pipe p);

    /**
     * Checks if the dependencies are satisfied
     *
     * @return true if the dependencies are satisfied, false otherwise
     */
    public boolean checkDependencies();

    // TODO this javaDoc
    public Integer countPipes(PipeType pipeType);

    /**
     * Achieves a string representation of the piping process
     *
     * @return the String representation of the pipe
     */
    @Override
    public String toString();
}
