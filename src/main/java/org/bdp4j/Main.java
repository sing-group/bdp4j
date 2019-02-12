package org.bdp4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.types.Instance;
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
    /* Logger instance for Main class */
    private static final Logger logger = LogManager.getLogger(Main.class);

    /* List of instances to process */
    private static List<Instance> carriers = new ArrayList<>();

    /* Singleton configuration instance */
    private static Configurator configurator = Configurator.getInstance();

    public static void main(String[] args) {
        /* Configure app from xml file */
        configurator.configureApp();

        /* Load pipes from jar */
        PipeProvider pipeProvider = new PipeProvider(configurator.getProp("pluginsFolder"));
        HashMap<String, PipeInfo> pipes = pipeProvider.getPipes();

        /* Configure pipe */
        Pipe p = configurator.configurePipe(pipes);
        System.out.println("\nPipe structure:\n\t" + p.toString() + "\n");

        /* Check dependencies */
        if (!p.checkDependencies()) {
            logger.error("[CHECK DEPENDENCIES] " + Pipe.getErrorMesage());
            System.exit(-1);
        }

        /* Load instances */
        generateInstances(configurator.getProp("samplesFolder"));

        /* Process instances */
        logger.info("Processing instances...");
        p.pipeAll(carriers);
        logger.info("Instances processed.");
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
            System.exit(0);
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
