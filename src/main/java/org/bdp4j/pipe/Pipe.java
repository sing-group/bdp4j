package org.bdp4j.pipe;

import org.bdp4j.types.Instance;
import org.bdp4j.types.PipeType;
import org.bdp4j.util.BooleanBean;

import java.util.Collection;
import java.util.List;

interface Pipe {
    /**
     * Process an Instance. This method takes an input Instance, destructively
     * modifies it in some way, and returns it. This is the method by which all
     * pipes are eventually run.
     * <p>
     * One can create a new concrete subclass of AbstractPipe simply by implementing
     * this method.
     *
     * @param carrier Instance to be processed.
     * @return Instancia procesada
     */
    Instance pipe(Instance carrier);

    /**
     * AbstractPipe all instances from a Collection
     *
     * @param carriers Collection of instances to pipe
     * @return The collection of instances after being processed
     */
    Collection<Instance> pipeAll(Collection<Instance> carriers);

    /**
     * Finds the parent AbstractPipe
     *
     * @return the parent AbstractPipe
     */
    AbstractPipe getParent();

    /**
     * Stablished the parent pipe for this one
     *
     * @param p The parent AbstractPipe for this one
     */
    void setParent(AbstractPipe p);

    /**
     * Finds the parent root
     *
     * @return the root parent
     */
    AbstractPipe getParentRoot();

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
    Class<?> getInputType();

    /**
     * Say datatype expected in the data attribute of a Instance
     *
     * @return the datatype expected in the data attribute of a Instance
     */
    Class<?> getOutputType();

    /**
     * Check if alwaysBeforeDeps are satisfied for pipe p (inserted). Initially deps contain
     * all alwaysBefore dependences for p. These dependencies are deleted (marked as resolved)
     * by recursivelly calling this method.
     *
     * @param p    The pipe that is being checked
     * @param deps The dependences that are not confirmed in a certain moment
     * @return null if not sure about the fullfulling, true if the dependences are satisfied,
     * false if the dependences could not been satisfied
     */
    Boolean checkAlwaysBeforeDeps(AbstractPipe p, List<Class<?>> deps);

    /**
     * Check if notBeforeDeps are satisfied for pipe p recursivelly. Note that p should be inserted.
     *
     * @param p      The pipe that is being checked
     * @param foundP // TODO what is this for?
     * @return null if not sure about the fullfulling, true if the dependences are satisfied,
     * false if the dependences could not been satisfied
     */
    boolean checkNotAfterDeps(AbstractPipe p, BooleanBean foundP);

    /**
     * Checks if current pipe contains the pipe p
     *
     * @param p The pipe to search
     * @return true if this pipe contains p false otherwise
     */
    boolean containsPipe(AbstractPipe p);

    /**
     * Checks if the dependencies are satisfied
     *
     * @return true if the dependencies are satisfied, false otherwise
     */
    boolean checkDependencies();

    // TODO this javaDoc
    Integer countPipes(PipeType pipeType);

    /**
     * Achieves a string representation of the piping process
     *
     * @return the String representation of the pipe
     */
    @Override
    String toString();
}
