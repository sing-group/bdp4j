package org.bdp4j.pipe;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ia.types.Instance;

public class ParallelPipes extends Pipe {
    private static final Logger logger = LogManager.getLogger(ParallelPipes.class);
    private Class<?> inputType = null;
    private Class<?> outputType = null;

    private ArrayList<Pipe> pipes;

    public ParallelPipes() {
        this.pipes = new ArrayList<>();
    }

    @Override
    public Instance pipe(Instance carrier) {
        return null;
    }

    public void add(Pipe pipe, boolean isOutput) {
        if (pipes.isEmpty()) {
            inputType = pipe.getInputType();
        } else {
            if (pipes.get(pipes.size() - 1).getInputType() != pipe.getInputType()) {
                logger.error("[PARALLEL PIPE ADD] BAD compatibility between Pipes.");
                System.exit(0);
            }
        }

        if (isOutput) outputType = pipe.getOutputType();

        pipes.add(pipe);
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
