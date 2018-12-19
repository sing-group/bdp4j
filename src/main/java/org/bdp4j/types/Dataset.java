package org.bdp4j.types;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.PipeParameter;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.converters.CSVSaver;
/**
 * Build a weka dataset
 *
 * @author María Novo
 */
public class Dataset implements Serializable {

    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Dataset.class);

    /**
     * The default value for the output file
     */
    public static final String DEFAULT_OUTPUT_FILE = "CSVDataset.csv";
    private String outputFile = DEFAULT_OUTPUT_FILE;

    /**
     * The default value for the dataset
     */
    private Instances dataset = null;

    /**
     * Default constructor, creates a new Dataset from dataset
     *
     * @param dataset to create a new Dataset
     */
    public Dataset(Dataset dataset) {
        this.dataset = new Instances(dataset.getWekaDataset());
    }

    /**
     * Default constructor, creates a new Dataset
     *
     * @param name The name of the relation
     * @param attributes The attribute list of dataset
     * @param capacity The initial capacity of the dataset
     */
    public Dataset(String name, ArrayList<Attribute> attributes, int capacity) {
        this.dataset = new Instances(name, attributes, capacity);
    }

    /**
     * Default constructor, creates a new Dataset
     *
     * @param name The name of the relation
     * @param attributes The attribute list of dataset
     * @param capacity The initial capacity of the dataset
     * @param outputFile The output file name, only in case you can export
     * dataset to an output file.
     */
    public Dataset(String name, ArrayList<Attribute> attributes, int capacity, String outputFile) {
        this(name, attributes, capacity);
        this.outputFile = outputFile;
    }

    /**
     * Set the output filename to store the CSV contents
     *
     * @param outputFile The filename/filepath to store the CSV contents
     */
    @PipeParameter(name = "outputFile", description = "Indicates the output filename/path for saving CSV", defaultValue = DEFAULT_OUTPUT_FILE)
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Returns the filename where the CSV contents will be stored
     *
     * @return the filename/filepath where the CSV contents will be stored
     */
    public String getOutputFile() {
        return this.outputFile;
    }

    /**
     * Returns the number of attributes in dataset
     *
     * @return the number of attributes in dataset
     */
    public int numAttributes() {
        return dataset.numAttributes();
    }

    /**
     * Creates a Dense instance
     *
     * @return The DenseInstance created
     */
    public Instance createDenseInstance() {
        dataset.add(new DenseInstance(this.numAttributes()));
        return dataset.lastInstance();
    }

    /**
     * Get an Instances from dataset
     *
     * @return an Instances from dataset
     */
    public Instances getWekaDataset() {
        return new Instances(dataset);
    }

    /**
     * Print the dataset content
     */
    public void printLine() {
        dataset.stream().forEach(System.out::println);
    }

    /**
     * Generates a CSV with dataset content.
     */
    public void generateCSV() {
        CSVSaver saver = new CSVSaver();
        try {
            File file = new File(outputFile);
            saver.setFile(file);
            saver.setInstances(dataset);
            //WEKA uses this fieldSeparator. In other case, you can't load this file in WEKA application
            saver.setFieldSeparator(",");
            saver.writeBatch();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }

    }

    /**
     * Get the attributes list of dataset
     *
     * @return A list with the attributes of dataset
     */
    public List<String> getAttributes() {
        Enumeration<Attribute> attributeEnum = dataset.enumerateAttributes();
        List<String> attributeList = new ArrayList<>();
        while (attributeEnum.hasMoreElements()) {
            attributeList.add(attributeEnum.nextElement().name());
        }
        return attributeList;
    }

    /**
     * Get the instance list of dataset
     *
     * @return A list with the instances of dataset
     */
    public List<Instance> getInstances() {

        Enumeration<Instance> instanceEnum = dataset.enumerateInstances();
        List<Instance> instanceList = new ArrayList<>();
        while (instanceEnum.hasMoreElements()) {
            instanceList.add(instanceEnum.nextElement());
        }
        return instanceList;
    }

    /*
    
    public static void main(String[] args) {

        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);
        //Se define la lista de transformadores
        Map<String, Transformer<? extends Object>> transformersList = new HashMap<>();
        transformersList.put("date", new Date2MillisTransformer());
        transformersList.put("target", new Enum2IntTransformer(transformList));

        String filePath = "outputsyns.csv";//Main.class.getResource("/outputsyns.csv").getPath();
        DatasetFromFile jml = new DatasetFromFile(filePath, transformersList);
        Dataset dataset = jml.loadFile();

        Instances data = dataset.getWekaDataset();
        AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
        //CfsSubsetEval eval = new CfsSubsetEval();
        GreedyStepwise search = new GreedyStepwise();
        search.setSearchBackwards(true);
        J48 base = new J48();
        BayesNet bnet = new BayesNet();
        classifier.setClassifier(base);
        classifier.setEvaluator(evaluator);
        //classifier.setEvaluator(eval);
        classifier.setSearch(search);
        int k = 3;
        Evaluation evaluation;
        //data.deleteStringAttributes();
        try {
            data.setClassIndex(data.numAttributes() - 1);
            evaluation = new Evaluation(data);
            evaluation.crossValidateModel(classifier, data, k, new Random(1));
           // System.out.println(evaluation.toSummaryString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
     */
}
