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

import org.bdp4j.types.Instance;
import org.bdp4j.types.PipeType;
import org.bdp4j.util.BooleanBean;

import java.util.Collection;
import java.util.List;

/**
 * Pipe interface should be used only then you have to extend from a third class
 * and you can not extend from AbstractPipe (only implementing an interface). Most of 
 * BDP4J services are implemented in AbstractPipe class. In order to create a Pipe,
 * if you extend from AbstractPipe class, only implementing pipe method is required.
 * So, we really disadvice the usage of this interface to use BDP4J. Use this interface
 * only in the case that there is no other option.
 * 
 * Pipe interface is the most difficult way to implement a Pipe. You have to implement 
 * a large collection of methods to make them compatible with BDP4J. 
 * 
 * @since Java 1.8
 * @author Jeray Lage
 * @author María Novo
 * @author José Ramón Méndez
 */
public interface Pipe {

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
    public Instance pipe(Instance carrier);

    /**
     * Pipe all instances from a Collection
     *
     * @param carriers Collection of instances to pipe
     * @return The collection of instances after being processed
     */
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
