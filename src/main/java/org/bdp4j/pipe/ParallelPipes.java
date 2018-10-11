package org.bdp4j.pipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;

import java.util.ArrayList;

public class ParallelPipes extends Pipe {
    private static final Logger logger = LogManager.getLogger(ParallelPipes.class);
    private Class<?> inputType = null;
    private Class<?> outputType = null;

    private ArrayList<Pipe> pipes;

    public ParallelPipes() {
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

    @Override
    public Class<?> getInputType() {
        return inputType;
    }

    @Override
    public Class<?> getOutputType() {
        return outputType;
    }
}
