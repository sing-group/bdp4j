package org.bdp4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.Instance;
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
 * Test pipe functionality
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    /**
     * List of instances to process
     */
    private static List<Instance> carriers = new ArrayList<>();

    public static void main(String[] args) {
        /* Load pipes from jar */
        PipeProvider pipeProvider = new PipeProvider();
        HashMap<String, Pipe> pipes = pipeProvider.serviceImpl();

        /* Load instances */
        generateInstances("./samples/");

        /* Create the processing pipe */
        Pipe p = null;
        try {
            p = new SerialPipes(new Pipe[]{
                    pipes.get("File2TargetAssignPipe"),
                    pipes.get("FilesizePipe"),
                    pipes.get("File2StringPipe"),
                    pipes.get("MeasureLengthPipe"),
                    pipes.get("GenerateOutputPipe")
            }
            );
        } catch (NullPointerException e) {
            logger.error("[PIPE GENERATION] Some Pipes does not exist.");
            System.exit(-1);
        }

        /* Check dependencies */
        if (!p.checkDependencies()) {
            logger.error("[CHECK DEPENDENCIES] " + Pipe.getErrorMesage());
            System.exit(-1);
        }

        /* Process instances */
        p.pipeAll(carriers);
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