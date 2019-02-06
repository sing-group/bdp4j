package org.bdp4j.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
    private static final Logger logger = LogManager.getLogger(PipeProvider.class);

    private PipeProvider() {
        File location = new File("plugins");

        File[] fileList = location.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));

        assert fileList != null;
        URL[] urls = new URL[fileList.length];
        for (int i = 0; i < fileList.length; i++) {
            try {
                urls[i] = fileList[i].toURI().toURL();
            } catch (MalformedURLException e) {
                logger.error("Malformed URL Exception.");
            }
        }

        URLClassLoader urlClassLoader = new URLClassLoader(urls);

        loader = ServiceLoader.load(Pipe.class, urlClassLoader);
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
