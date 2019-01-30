package org.bdp4j.sample;

import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.sample.pipe.impl.*;
import org.bdp4j.types.Instance;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class for demo.
 *
 * @author Yeray Lage
 */
public class Main {
    /**
     * List of instances to process
     */
    static List<Instance> carriers = new ArrayList<>();

    public static void main(String[] args) {
        /* Load instances */
        generateInstances("./samples/");

        /* Create the preprocessing pipe */
        Pipe p = new SerialPipes(
                new Pipe[]{
                        new File2TargetAssignPipe(),
                        new FilesizePipe(),
                        new File2StringPipe(),
                        new MeasureLengthPipe(),
                        new GenerateOutputPipe()
                }
        );

        /* Check dependencies */
        if (!p.checkDependencies()) {
            System.out.println(Pipe.getErrorMesage());
            System.exit(-1);
        }

        /* Process instances */
        p.pipeAll(carriers);
    }

    /**
     * Generate a instance List on instances attribute by recursivelly finding
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
         * Include a filne in the instancelist
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