package org.bdp4j.pipe;

import java.util.Arrays;
import java.util.List;

import org.bdp4j.ia.types.Instance;

public class CompositedPipe extends Pipe {

    //TODO what data types does this Pipe has?
    @Override
    public Class<?> getInputType() {
        return null;
    }

    @Override
    public Class<?> getOutputType() {
        return null;
    }

    private final List<Pipe> pipes;

    CompositedPipe(Pipe... pipes) {
        for (int i = 0; i < pipes.length; i++) {
            if (pipes[i] == null)
                throw new NullPointerException("Each pipe must be not null");
        }

        this.pipes = Arrays.asList(pipes.clone());
    }

    public CompositedPipe(List<? extends Pipe> pipes) {
        this(pipes.toArray(new Pipe[0]));
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (carrier == null)
            throw new IllegalArgumentException("the carrier must not be null");
        for (Pipe pipe : pipes) {
            carrier = pipe.pipe(carrier);
        }
        return carrier;
    }

}
