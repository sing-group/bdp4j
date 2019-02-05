package org.bdp4j.sample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.ml.DatasetFromFile;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.sample.pipe.impl.*;
import org.bdp4j.transformers.Enum2IntTransformer;
import org.bdp4j.types.Instance;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.PipeProvider;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        /* Load pipes */
        ArrayList<Pipe> pipeFromList = PipeProvider.getInstance().serviceImpl();
        for (Pipe pipeFromJar : pipeFromList)
            logger.info("[PIPE LOAD] " + pipeFromJar.getClass().getSimpleName() + " loaded.");

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

        //Then load the dataset to use it with Weka TM
        Map<String, Integer> targetValues = new HashMap<>();
        targetValues.put("ham", 0);
        targetValues.put("spam", 1);

        //Lets define transformers for the dataset
        Map<String, Transformer> transformersList = new HashMap<>();
        transformersList.put("target", new Enum2IntTransformer(targetValues));

        Instances data = (new DatasetFromFile(GenerateOutputPipe.DEFAULT_FILE, transformersList)).loadFile().getWekaDataset();

        data.deleteStringAttributes();
        data.setClassIndex(data.numAttributes() - 1);
        System.out.println("Instance no: " + data.numInstances());
        System.out.println("Attritubes no: " + data.numAttributes());
        System.out.println("Target Attribute index: " + data.classIndex());

        try {
            System.out.println("------------------------------------------");
            System.out.println("--------- Naive Bayes Classifier ---------");
            System.out.println("------------------------------------------");
            Evaluation nvEvaluation = new Evaluation(data);
            nvEvaluation.crossValidateModel(new NaiveBayes(), data, 10, new Random(1));

            System.out.println("Summary: ");
            System.out.println("------------------------------------------");
            System.out.println(">> TN: " + nvEvaluation.confusionMatrix()[0][0]);
            System.out.println(">> FP: " + nvEvaluation.confusionMatrix()[0][1]);
            System.out.println(">> FN: " + nvEvaluation.confusionMatrix()[1][0]);
            System.out.println(">> TP: " + nvEvaluation.confusionMatrix()[1][1]);
        } catch (Exception ex) {
            System.out.println("Error executing Naïve Bayes: " + ex.getMessage());
            ex.printStackTrace();
        }
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