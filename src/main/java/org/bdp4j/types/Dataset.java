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

    /**
     * The Deafult output file for CSVs
     */
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
        this.outputFile = dataset.getOutputFile();
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
     * Print the dataset content using Standard output
     */
    public void printLine() {
        dataset.stream().forEach(System.out::println);
    }

    /**
     * Generates a CSV with the dataset contents.
     * The CSV will be saved in the file that store the outputFile.
     * See Dataset.setOutputFile()
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
     * Generate comments to apply a transformer list
     * @param transformersList The transformer list to generate the comments
     * @return A String to create the comments for the generation of arff files
     */
    private String getComments(Map<String, Transformer> transformersList) {
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
     *
     * @param transformersList The list of transformers
     * @param file  The destination file
     * @return The ARFF content
     */
    public String generateARFFWithComments(Map<String, Transformer> transformersList, String file) {
        String comments = getComments(transformersList);
        // Generate 
        Instances wekaDataset = this.getWekaDataset();
        if (file.length() == 0) {
            file = "WEKADatasetWithComments.arff";
        }
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

    /**
     * Get a list of synsets in Dataset
     *
     * @return a list of synsets in Dataset
     */
    public List<String> getSynsets() {
        List<String> synsetsList = new ArrayList<>();
        List<String> attributes = this.getAttributes();
        for (String attribute : attributes) {
            if (attribute.contains("bn:")) {
                synsetsList.add(attribute);
            }
        }
        return synsetsList;
    }

    /**
     * Replace synsets list with its hyperonym
     *
     * @param hyperonymList Hiperonyms list to replace the original synsets
     * @return Dataset with hyperonyms instead of original synsets
     */
    public Dataset replaceSynsetWithHyperonym(Map<String, String> hyperonymList) {
        Instances instances = this.dataset;

        for (Map.Entry<String, String> entry : hyperonymList.entrySet()) {
            String oldValue = entry.getKey();
            String newValue = entry.getValue();
            Attribute att = instances.attribute(oldValue);
            instances.renameAttribute(att, newValue);
        }

        return this;
    }

    /**
     * Delete attributes from Dataset
     *
     * @param listAttributeName  List of attributes to delete
     * @return Dataset without this list of attributes
     */
    public Dataset deleteAttributeColumns(List<String> listAttributeName) {
        Instances instances = this.dataset;
        for (String attributeName : listAttributeName) {
            int attPosition = instances.attribute(attributeName).index();
            if (attPosition >= 0) {
                instances.deleteAttributeAt(attPosition);
            }
        }
        return this;
    }

    /**
     * Delete all attributes from Dataset but synstetIds and target attributes
     * 
     * @return  Dataset only with synsetIds and target attributes
     */
    public Dataset getOnlySynsetIdColumns() {

        Instances instances = this.dataset;
        List<String> attributesToDelete = new ArrayList<>();
        List<String> attributes = this.getAttributes();
        for (String attribute : attributes) {
            if (!attribute.contains("bn:") && !attribute.equals("target")) {
                attributesToDelete.add(attribute);
            }
        }
        return this.deleteAttributeColumns(attributesToDelete);
    }

    public Dataset joinAttributeColumns(List<String> listAttributeNameToJoin, String newAttribute) {
        // TODO
        //this.deleteAttributeColumns(listAttributeNameToJoin);

        Instances dataset = this.dataset;

        // Create a new attributesList
        Enumeration<Attribute> attributesList = dataset.enumerateAttributes();
        ArrayList<Attribute> attributes = new ArrayList<>();
        Attribute attribute = new Attribute(newAttribute);
        while (attributesList.hasMoreElements()) {
            attributes.add(attributesList.nextElement());
        }
        attributes.add(attribute);

        Dataset newDataset = new Dataset("dataset", attributes, 0);

        int numOfInstances = this.getInstances().size();
        Double value = 0d;
        Instance instance = null;
        int positionNewAttribute = 0;
        for (int i = 0; i < numOfInstances; i++) {

            //Instance instance = dataset.instance(i);
            boolean exists = false;
            instance = newDataset.createDenseInstance();

//            for (String attributeName : listAttributeNameToJoin) {
//                value += instance.value(attribute);
//              //  exists = true;
//            }
            for (int index = 0; index < instance.numAttributes() - 1; index++) {
                Attribute currentAtt = instance.attribute(index);
                if (listAttributeNameToJoin.contains(currentAtt.name())) {
                    if (value == 0) {
                        positionNewAttribute = index;
                        instance = dataset.instance(i);
                    }
                    value += instance.value(currentAtt);
                } else {
                    instance = dataset.instance(i);
                }
            }

        }

        instance.setValue(positionNewAttribute, value);

        return this;
    }

    @Override
    public Dataset clone() {
        return new Dataset(this);
    }
}
