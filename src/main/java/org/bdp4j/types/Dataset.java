package org.bdp4j.types;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
public class Dataset {

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
    private Instances dataset = null;

    public Dataset(String name, ArrayList<Attribute> attributes, int capacity) {
        dataset = new Instances(name, attributes, capacity);
    }

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
     * @return the filename/filepath where the CSV contents will be stored
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
            saver.setFieldSeparator(";");
            saver.writeBatch();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
