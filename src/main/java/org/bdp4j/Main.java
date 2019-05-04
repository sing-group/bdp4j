/*
 * BDP4j implements a pipeline framework to allow definining
 * project pipelines from XML. The main goal of the pipelines of this
 * application is to transform imput data received from multiple sources
 * into fully qualified datasets to be used with Machine Learning.
 *
 * Copyright (C) 2018  Sing Group (University of Vigo)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.bdp4j;

import gui.JGraphX;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.types.Instance;
import org.bdp4j.types.PipeType;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.PipeInfo;
import org.bdp4j.util.PipeProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Test pipe functionality.
 *
 * @author Yeray Lage
 */
public class Main {
    /* For logging purposes */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /* List of instances to process */
    public static List<Instance> carriers = new ArrayList<>();

    /* Singleton configuration instance */
    private static Configurator configurator = Configurator.getInstance("./config/configuration.xml");

    public static void main(String[] args) {
        /* Configure app from xml file */
        configurator.configureApp();

        /* Load pipes from jar */
        PipeProvider pipeProvider = new PipeProvider(configurator.getProp(Configurator.PLUGINS_FOLDER));
        HashMap<String, PipeInfo> pipes = pipeProvider.getPipes();

        AbstractPipe p;
        if (args[0].equals("gui")) {
            /* GUI testing */
            p = new JGraphX(pipes).start();
        } else {
            /* Configure pipe */
            p = configurator.configurePipe(pipes);
        }

        logger.info("Pipe structure:\n\t" + p.toString() + "\n");
        for (PipeType pipeType : PipeType.values()) {
            logger.info("[PIPES COUNT] " + pipeType.name() + ": " + p.countPipes(pipeType));
        }
        /* Check dependencies */
        if (!p.checkDependencies()) {
            logger.fatal("[CHECK DEPENDENCIES] " + AbstractPipe.getErrorMessage());
            System.exit(-1);
        }

        /* Load instances */
        generateInstances(configurator.getProp(Configurator.SAMPLES_FOLDER));

        /* Process instances */
        logger.info("Processing " + carriers.size() + " instances...");
        long init = System.currentTimeMillis();
        p.pipeAll(carriers);
        logger.info("Instances processed in " + (System.currentTimeMillis() - init) + "ms.");

        System.exit(0);
    }

    /**
     * Generate a instance List on instances attribute by recursively finding
     * all files included in testDir directory
     *
     * @param testDir The directory where the instances should be loaded
     */
    private static void generateInstances(String testDir) {
        try {
            Files.walk(Paths.get(testDir))
                    .filter(Files::isRegularFile)
                    .forEach(FileMng::visit);
        } catch (IOException e) {
            System.exit(-1);
        }
    }

    /**
     * Used to add a new instance on instances attribute when a new file is
     * detected.
     */
    static class FileMng {
        /**
         * Include a file in the instance list
         *
         * @param path The path of the file
         */
        static void visit(Path path) {
            File data = path.toFile();
            String target = null;
            String name = data.getPath();
            File source = data;

            carriers.add(new Instance(data, target, name, source));
        }
    }
}
