package org.bdp4j;

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
    private static List<Instance> carriers = new ArrayList<>();

    /* Singleton configuration instance */
    private static Configurator configurator = Configurator.getInstance("./config/configuration.xml");

    public static void main(String[] args) {
        /* Configure app from xml file */
        configurator.configureApp();

        /* Load pipes from jar */
        PipeProvider pipeProvider = new PipeProvider(configurator.getProp(Configurator.PLUGINS_FOLDER));
        HashMap<String, PipeInfo> pipes = pipeProvider.getPipes();

        /* Configure pipe */
        AbstractPipe p = configurator.configurePipe(pipes);
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
