package org.bdp4j.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.PipeParameter;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build a weka dataset
 *
 * @author María Novo
 */
public class Dataset implements Serializable, Cloneable {

    /**
     * Function to combine columns by summing (used for frequency/count values)
     */
    public static final CombineOperator COMBINE_SUM = new CombineOperator() {
        @Override
        public Double combine(Double a, Double b) {
            return a + b;
        }
    };

    /**
     * Function to combine columns by OR (used for binary representation)
     */
    public static final CombineOperator COMBINE_OR = new CombineOperator() {
        @Override
        public Double combine(Double a, Double b) {
            return (a > 0 || b > 0) ? 1d : 0d;
        }
    };

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
     * Generates a CSV with the dataset contents. The CSV will be saved in the
     * file that store the outputFile. See Dataset.setOutputFile()
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
     *
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
     * @param file The destination file
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
     * Get a a list of columns names that matches with pattern
     *
     * @param pattern Pattern to filter column names
     * @return a list of columns names that matches with pattern
     */
    public List<String> filterColumnNames(String pattern) {
        Pattern p = Pattern.compile(pattern);

        List<String> columnNamesList = new ArrayList<>();
        List<String> attributes = this.getAttributes();
        for (String attribute : attributes) {
            Matcher m = p.matcher(attribute);
            if (m.find()) {
                columnNamesList.add(attribute);
            }
        }
        return columnNamesList;
    }

    /**
     * Replace the column names with the indicated name. In case of replace
     * several columns with the same name, they are combined by adding values
     *
     * @param newColumnNames List to replace the original name with other one
     * @param op The column combining operator
     * @return Dataset with new columns names
     */
    public Dataset replaceColumnNames(Map<String, String> newColumnNames, CombineOperator op) {
        Instances instances = this.dataset;
        List<String> listAttributeName = new ArrayList<>();
        for (Map.Entry<String, String> entry : newColumnNames.entrySet()) {
            String oldValue = entry.getKey();
            String newValue = entry.getValue();
            try {
                Attribute lastAttribute = instances.attribute(newValue);
                Attribute att = instances.attribute(oldValue);
                if (lastAttribute == null) {
                    instances.renameAttribute(att, newValue);
                } else {
                    for (Instance instance : instances) {
                        Double lastAttValue = instance.value(lastAttribute.index());
                        Double oldAttValue = instance.value(att.index());
                        Double combineValues = op.combine(lastAttValue, oldAttValue);
                        instance.setValue(lastAttribute, combineValues);

                    }
                    listAttributeName.add(oldValue);
                    deleteAttributeColumns(listAttributeName);
                }

            } catch (NullPointerException ex) {
                logger.warn(" Attribute name doesn't exist. " + ex.getMessage());
            }
        }

        return this;
    }

    /**
     * Delete all attributes from Dataset except all that match with pattern
     *
     * @param pattern Param for filtering.
     * @return Dataset only with attributes that match with pattern
     */
    public Dataset filterColumns(String pattern) {
        Pattern p = Pattern.compile(pattern);
        Instances instances = this.dataset;
        List<String> attributesToDelete = new ArrayList<>();
        List<String> attributes = this.getAttributes();

        for (String attribute : attributes) {
            Matcher m = p.matcher(attribute);
            if (!m.find()) {
                attributesToDelete.add(attribute);
            }
        }
        return this.deleteAttributeColumns(attributesToDelete);
    }

    /**
     * Delete attributes from Dataset
     *
     * @param listAttributeName List of attributes to delete
     * @return Dataset without this list of attributes
     */
    public Dataset deleteAttributeColumns(List<String> listAttributeName) {
        Instances instances = this.dataset;
        for (String attributeName : listAttributeName) {
            try {
                int attPosition = instances.attribute(attributeName).index();
                if (attPosition >= 0) {
                    instances.deleteAttributeAt(attPosition);
                }
            } catch (NullPointerException ex) {
                logger.warn(Dataset.class.getClass().getName() + ". Attribute >>" + attributeName + "<< doesn't exist. " + ex.getMessage());
            }
        }
        return this;
    }

    /**
     * Join attribute columns
     *
     * @param listAttributeNameToJoin The name of colums that should be joined
     * @param newAttributeName The name of the new column  
     * @param op Operator that indicates the type of operation to do to combine columns
     * @return A Dataset where some columns have been combined
     */
    //TODO: change the name to joinAttributes or joinColumns (is more clear)
    public Dataset joinAttributeColumns(List<String> listAttributeNameToJoin, String newAttributeName, CombineOperator op) {
        Instances instances = this.dataset;

        try {
            for (Instance instance : instances) {
                Double newAttributeValue = 0d;
                boolean isFirstInstance = instances.firstInstance().equals(instance);

                for (String attributeToJoin : listAttributeNameToJoin) {
                    try {
                        boolean isFirstAtt = listAttributeNameToJoin.get(0).equals(attributeToJoin);
                        Double attributeValue;

                        if (isFirstAtt && !isFirstInstance) {
                            attributeValue = instance.value(instances.attribute(newAttributeName).index());
                        } else {
                            attributeValue = instance.value(instances.attribute(attributeToJoin).index());
                            if (isFirstAtt && isFirstInstance) {
                                Attribute attribute = instances.attribute(attributeToJoin);
                                instances.renameAttribute(attribute, newAttributeName);
                            }
                        }
                        newAttributeValue = op.combine(newAttributeValue, attributeValue);
                        instance.setValue(instances.attribute(newAttributeName), newAttributeValue);

                    } catch (NullPointerException ex) {
                        logger.warn(Dataset.class.getClass().getName() + ". Attribute >>" + attributeToJoin + "<< doesn't exist. " + ex.getMessage());
                    }
                }
            }
            deleteAttributeColumns(listAttributeNameToJoin);
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
        return this;
    }

    @Override
    public Dataset clone() {
        return new Dataset(this);
    }

    /**
     * Interface that defines the operation to combine 2 columns
     */
    public interface CombineOperator {

        /**
         * Combine values of an atrribute for two columns
         *
         * @param a the value on the first column
         * @param b the value on the second column
         * @return The result
         */
        Double combine(Double a, Double b);
    }
}
