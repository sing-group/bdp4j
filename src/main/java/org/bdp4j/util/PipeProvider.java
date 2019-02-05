package org.bdp4j.util;

import org.bdp4j.pipe.Pipe;

import java.util.ArrayList;
import java.util.ServiceLoader;

/**
 * Class for loading jar with new Pipes dynamically
 *
 * @author Yeray Lage
 */
public class PipeProvider {
    private static PipeProvider provider;
    private ServiceLoader<Pipe> loader;

    private PipeProvider() {
        loader = ServiceLoader.load(Pipe.class);
    }

    public static PipeProvider getInstance() {
        if (provider == null) {
            provider = new PipeProvider();
        }
        return provider;
    }

    public ArrayList<Pipe> serviceImpl() {
        ArrayList<Pipe> pipeList = new ArrayList<>();

        for (Pipe pipe : loader) {
            pipeList.add(pipe);
        }

        return pipeList;
    }
}
