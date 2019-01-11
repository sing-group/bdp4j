package org.bdp4j.types;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.PipeParameter;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

/**
 * Build a weka dataset
 *
 * @author María Novo
 */
public class Dataset implements Serializable, Cloneable {

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
    
    public String getComments(Map<String, Transformer> transformersList){
        // Get information about transformers to add to arff file
        StringBuilder comments = new StringBuilder();
        for (Map.Entry<String, Transformer> entry : transformersList.entrySet()) {
            String key = entry.getKey();
            Transformer value = entry.getValue();
            Class<?> transformerClass = value.getClass();
            String transformersListValues = value.getTransformerListValues();
            comments.append("% ");
            comments.append(key).append(": ").append(transformerClass.getSimpleName());
            if (transformersListValues != null) {
                comments.append(" --> ").append(transformersListValues);
            }
            comments.append("\n");
        }
        return comments.toString();
    }

    /**
     * Generates a CSV with dataset content.
     * @param transformersList The list of transformers
     * @return The ARFF content
     */
    public String generateARFFWithComments(Map<String, Transformer> transformersList) {
       String comments = getComments(transformersList);
        // Generate 
        Instances wekaDataset = this.getWekaDataset();

        String file = "WEKADatasetWithComments.arff";

        try (OutputStream outputStream = new FileOutputStream(new File(file))) {

            ArffSaver saver = new ArffSaver();
            saver.setInstances(wekaDataset);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));

            bw.write(comments + "\n");
            bw.write("\n");
            bw.flush();

            saver.setDestination(outputStream);
            saver.writeBatch();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return file;
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

    public List<String> getSynsets(){
        List<String> synsetsList = new ArrayList<>();
        List<String> attributes =  this.getAttributes();
        for (String attribute : attributes) {
            if (attribute.contains("bn:")){
                synsetsList.add(attribute);
            }
        }
        return synsetsList;
    }

}
