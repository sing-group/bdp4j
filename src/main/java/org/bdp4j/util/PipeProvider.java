package org.bdp4j.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.ServiceLoader;

/**
 * Class for loading jar with new Pipes dynamically
 *
 * @author Yeray Lage
 */
public class PipeProvider {
    private ServiceLoader<Pipe> loader;
    private static final Logger logger = LogManager.getLogger(PipeProvider.class);

    public PipeProvider() {
        File location = new File("plugins");

        File[] fileList = location.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));

        assert fileList != null;
        URL[] urls = new URL[fileList.length];
        for (int i = 0; i < fileList.length; i++) {
            try {
                urls[i] = fileList[i].toURI().toURL();
                logger.info("[FILE READING] " + fileList[i].getName() + " successfully read.");
            } catch (MalformedURLException e) {
                logger.error("[FILE READING] Malformed URL Exception.");
            }
        }

        URLClassLoader urlClassLoader = new URLClassLoader(urls);

        loader = ServiceLoader.load(Pipe.class, urlClassLoader);
    }

    public HashMap<String, Pipe> serviceImpl() {
        HashMap<String, Pipe> pipeList = new HashMap<>();

        for (Pipe pipe : loader) {
            pipeList.put(pipe.getClass().getSimpleName(), pipe);
            logger.info("[PIPE LOAD] " + pipe.getClass().getSimpleName() + " OK.");
        }

        return pipeList;
    }
}
