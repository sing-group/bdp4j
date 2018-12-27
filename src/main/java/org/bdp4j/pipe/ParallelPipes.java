package org.bdp4j.pipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bdp4j.util.BooleanBean;


/**
 * A class implementing parallel procesing of instances
 * @author Yeray Lage
 */
public class ParallelPipes extends Pipe {
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
    private ArrayList<Pipe> pipes;

    /**
     * Default constuctor
     */
    public ParallelPipes() {
        super(new Class<?>[0], new Class<?>[0]);
        this.pipes = new ArrayList<>();
    }

    @Override
    public Instance pipe(Instance original) {

        if (outputType == null) {
            logger.error("[PARALLEL PIPE] Not output Pipe declared.");
            System.exit(0);
        }

        Instance originalCopy = new Instance(original); // Copy instance of original for saving Data state.

        for (Pipe p : pipes) {
            logger.info("PARALLEL PIPE " + p.getClass().getName());


            try {
                if (p.equals(pipes.get(0))) {
                    // If first pipe, it is the output one. Then we use the original one.
                    original = p.pipe(original);
                } else {
                    Instance copy = new Instance(originalCopy); // One copy for each pipe of parallel.

                    if (copy.isValid()) {
                        logger.info("INST " + copy.getName());
                        p.pipe(copy); // Just process pipe for properties set.
                    } else {
                        logger.info("Skipping invalid instance " + copy.toString());
                    }
                }
            } catch (Exception e) {
                logger.fatal("Exception caught on pipe " + p.getClass().getName() + ". " + e.getMessage() + " while processing instance");
                e.printStackTrace(System.err);
                System.exit(0);
            }

        }

        return original;
    }

    /**
     * Adds another pipe that is executed in parallel
     * @param pipe The new pipe added
     * @param isOutput Marks if this pipe should be conducted to the next pipe.
     */
    public void add(Pipe pipe, boolean isOutput) {
        // Set input type and check if valid.
        if (pipes.isEmpty()) {
            inputType = pipe.getInputType();
        } else if (pipes.get(pipes.size() - 1).getInputType() != pipe.getInputType()) {
            logger.error("[PARALLEL PIPE ADD] BAD compatibility between Pipes.");
            System.exit(0);
        }

        // Set output type and put output Pipe as first of array.
        if (isOutput && !pipes.isEmpty()) {
            outputType = pipe.getOutputType();

            Pipe first = pipes.get(0);
            pipes.set(0, pipe); // Set output pipe as first.
            pipes.add(first);
        } else {
            if (isOutput) outputType = pipe.getOutputType();
            pipes.add(pipe);
        }
    }

    /**
     * Determines the input type for the pipe
     * @return the input type (Instance.data) for the pipe
     */
    @Override
    public Class<?> getInputType() {
        return inputType;
    }

    /**
     * Determines the output type (Instance.data) for the pipe
     * @return the output type (Instance.data) for the pipe
     */
    @Override
    public Class<?> getOutputType() {
        return outputType;
    }

    /**
     * Check if alwaysBeforeDeps are satisfied for pipe p. Initially deps contain
     * all alwaysBefore dependences for p. These dependencies are deleted (marked as resolved)
     * by recursivelly calling this method.
     * @param p The pipe that is being checked
     * @param deps The dependences that are not confirmed in a certain moment
     * @return null if not sure about the fullfulling, true if the dependences are satisfied, 
     *    false if the dependences could not been satisfied 
     */
    @Override
    Boolean checkAlwaysBeforeDeps(Pipe p, List<Class<?>> deps){
        if (!containsPipe(p)){
            for (Pipe p1:this.pipes){
                Boolean retVal=p1.checkAlwaysBeforeDeps(p, deps);
                if (retVal!=null) return retVal;
           }            
        }else{
            for (Pipe p1:this.pipes){
                if (p1.containsPipe(p)){
                    Boolean retVal=p1.checkAlwaysBeforeDeps(p, deps);
                    if (retVal!=null) return retVal;
                    else return deps.size()==0; //In this situation deps.size() should no be 0       
                }
            }    
        }

        return null;
    }

    /**
     * Check if notBeforeDeps are satisfied for pipe p recursivelly. Note that p should be inserted.
     * @param p The pipe that is being checked
     * @return null if not sure about the fullfulling, true if the dependences are satisfied, 
     *    false if the dependences could not been satisfied 
     */
    @Override
    boolean checkNotAfterDeps(Pipe p, BooleanBean foundP){
        boolean retVal=true;

        if (!containsPipe(p)){
            for (Pipe p1:this.pipes){
                if (p1 instanceof SerialPipes || p1 instanceof ParallelPipes)
                    retVal=retVal&&p1.checkNotAfterDeps(p,foundP);
                else {
                    if(foundP.getValue()) retVal=retVal&&!(Arrays.asList(p.notAftterDeps).contains(p1.getClass()));
                    if (!retVal){
                        errorMessage="Unsatisfied NotAfter dependency for pipe "+p.getClass().getName()+" ("+p1.getClass().getName()+")";
                        return retVal;
                    }
                    foundP.Or(p==p1);
                }    
            }          
        }else{
            Pipe pipeThatContainsP=null;

            int i=0;
            for (;i<pipes.size()-1 && !pipes.get(i).containsPipe(p);i++);
            pipeThatContainsP=pipes.get(i);

            if(pipeThatContainsP!=null) { //Should be true
                if (pipeThatContainsP instanceof SerialPipes || pipeThatContainsP instanceof ParallelPipes)
                    retVal=retVal&&pipeThatContainsP.checkNotAfterDeps(p,foundP);
                else {
                    if(foundP.getValue()){
                        retVal=retVal&&!(Arrays.asList(p.notAftterDeps).contains(pipeThatContainsP.getClass()));
                        if (!retVal) {
                            errorMessage="Unsatisfied NotAfter dependency for pipe "+p.getClass().getName()+" ("+pipeThatContainsP.getClass().getName()+")";
                            return retVal;
                        }
                    } 
                    foundP.Or(p==pipeThatContainsP);
                }
            }

            for (Pipe p1:this.pipes){
                if (p1!=pipeThatContainsP){
                    if (p1 instanceof SerialPipes || p1 instanceof ParallelPipes)
                        retVal=retVal&&p1.checkNotAfterDeps(p,foundP);
                    else {
                        if(foundP.getValue()){
                            retVal=retVal&&!(Arrays.asList(p.notAftterDeps).contains(p1.getClass()));
                            if (!retVal) {
                                errorMessage="Unsatisfied NotAfter dependency for pipe "+p.getClass().getName()+" ("+p1.getClass().getName()+")";
                                return retVal;
                            }
                        } 
                        foundP.Or(p==p1);
                    }  
                }
            }
            
        }

        return retVal;
    }

    /**
     * Checks if current pipe contains the pipe p
     * @param p The pipe to search
     * @return true if this pipe contains p false otherwise
     */
    @Override
    public boolean containsPipe(Pipe p){
        for (Pipe p1:this.pipes){
             if (p1.containsPipe(p)) return true;
        }
        return false;
     } 
     
    /**
     * Checks if the dependencies are satisfied
     * @return true if the dependencies are satisfied, false otherwise
     */
    @Override
    public boolean checkDependencies(){
        boolean returnValue=true;

        for (Pipe p1:pipes){
            if (! (p1 instanceof SerialPipes) && ! (p1 instanceof ParallelPipes)){
               returnValue=returnValue&getParentRoot().checkAlwaysBeforeDeps(p1, new ArrayList<Class<?>>(Arrays.asList(p1.alwaysBeforeDeps)));
               returnValue=returnValue&getParentRoot().checkNotAfterDeps(p1, new BooleanBean(false));
            }else{
                returnValue=returnValue&p1.checkDependencies();
            }
        }

        return returnValue;
    }
}
