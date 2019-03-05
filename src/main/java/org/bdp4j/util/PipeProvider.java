package org.bdp4j.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;

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
    private ServiceLoader<AbstractPipe> loader;
    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(PipeProvider.class);

    /**
     * Takes all the jar from location and adds the pipes to the ServiceLoader
     *
     * @param folder The folder location
     */
    public PipeProvider(String folder) {
        File location = new File(folder);

        File[] fileList = location.listFiles(file -> file.getPath().toLowerCase().endsWith(".jar"));

        assert fileList != null;
        URL[] urls = new URL[fileList.length];
        for (int i = 0; i < fileList.length; i++) {
            try {
                urls[i] = fileList[i].toURI().toURL();
                logger.info("[JAR READING] " + fileList[i].getName() + " successfully read.");
            } catch (MalformedURLException e) {
                logger.error("[JAR READING] Malformed URL Exception.");
            }
        }

        URLClassLoader urlClassLoader = new URLClassLoader(urls);

        loader = ServiceLoader.load(AbstractPipe.class, urlClassLoader);
    }

    /**
     * Returns the pipeInfo of each AbstractPipe with their names as key.
     *
     * @return HashMap were key is the name of pipe and value is the AbstractPipe.
     */
    public HashMap<String, PipeInfo> getPipes() {
        HashMap<String, PipeInfo> pipeList = new HashMap<>();

        for (AbstractPipe pipe : loader) {

            PipeInfo pipeInfo = new PipeInfo(pipe.getClass().getSimpleName(), pipe.getClass());

            pipeList.put(pipeInfo.getPipeName(), pipeInfo);

            logger.info("[PIPE LOAD] " + pipeInfo.getPipeName() + " OK.");
        }

        return pipeList;
    }
}
