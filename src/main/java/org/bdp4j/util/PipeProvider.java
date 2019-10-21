/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


package org.bdp4j.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.ServiceLoader;

public class PipeProvider {

    private ServiceLoader<Pipe> loader;
    private static final Logger logger = LogManager.getLogger(PipeProvider.class);

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

        loader = ServiceLoader.load(Pipe.class, urlClassLoader);
    }

    /**
     * Returns the pipeInfo of each AbstractPipe with their names as key.
     *
     * @return HashMap were key is the name of pipe and value is the
     * AbstractPipe.
     */
    public HashMap<String, PipeInfo> getPipes() {
        HashMap<String, PipeInfo> pipeList = new HashMap<>();

        for (Pipe pipe : loader) {

            PipeInfo pipeInfo = new PipeInfo(pipe.getClass().getSimpleName(), pipe.getClass());

            boolean transformationPipe = (pipe.getClass().getAnnotation(TransformationPipe.class) != null);
            if (!transformationPipe) {
                if (!pipe.getInputType().equals(pipe.getOutputType())) {
                    logger.fatal("[GET PIPES] Error checking types in pipe " + pipe.getClass().getSimpleName());
                    Configurator.setIrrecoverableErrorInfo("[GET PIPES] Error checking types in pipe " + pipe.getClass().getSimpleName());
                    Configurator.getActionOnIrrecoverableError().run();
                }
            }

            pipeList.put(pipeInfo.getPipeName(), pipeInfo);

            logger.info("[PIPE LOAD] " + pipeInfo.getPipeName() + " OK.");
        }

        return pipeList;
    }
}
