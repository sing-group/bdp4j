package org.bdp4j.pipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.util.BooleanBean;

import java.util.Collection;
import java.util.List;

interface PipeInterface {

    /**
     * For logging purposes
     */
    Logger logger = LogManager.getLogger(Pipe.class);
    /**
     * Error message for dependencies
     */
//    String errorMessage = null;
    /**
     * Dependencies of the type alwaysBefore
     * These dependences indicate what pipes must be
     * executed before the current one.
     */
    Class<?>[] alwaysBeforeDeps = null;
    /**
     * Dependencies of the type notAfter
     * These dependences indicate what pipes must not be
     * executed after the current one.
     */
    Class<?>[] notAfterDeps = null;
    /**
     * Marks if the next instance to pipe will be the last one to pipe
     */
    boolean isLast = false;
    /**
     * The parent pipe for this one
     */
    Pipe parent = null;

    /**
     * Get the error Message dependencies
     *
     * @return The error message
     */
//    static String getErrorMessage() {
//        return errorMessage;
//    }

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
    Instance pipe(Instance carrier);

    /**
     * Pipe all instances from a Collection
     *
     * @param carriers Collection of instances to pipe
     * @return The collection of instances after being processed
     */
    Collection<Instance> pipeAll(Collection<Instance> carriers);


    /**
     * Finds the parent Pipe
     *
     * @return the parent Pipe
     */
    Pipe getParent();

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
    Pipe getParentRoot();

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
    Boolean checkAlwaysBeforeDeps(Pipe p, List<Class<?>> deps);

    /**
     * Check if notBeforeDeps are satisfied for pipe p recursivelly. Note that p should be inserted.
     *
     * @param p      The pipe that is being checked
     * @param foundP // TODO what is this for?
     * @return null if not sure about the fullfulling, true if the dependences are satisfied,
     * false if the dependences could not been satisfied
     */
    boolean checkNotAfterDeps(Pipe p, BooleanBean foundP);

    /**
     * Checks if current pipe contains the pipe p
     *
     * @param p The pipe to search
     * @return true if this pipe contains p false otherwise
     */
    boolean containsPipe(Pipe p);

    /**
     * Checks if the dependencies are satisfied
     *
     * @return true if the dependencies are satisfied, false otherwise
     */
    boolean checkDependencies();

    // TODO this javaDoc
    Integer teePipesCount();

    /**
     * Achieves a string representation of the piping process
     *
     * @return the String representation of the pipe
     */
    @Override
    String toString();
}
