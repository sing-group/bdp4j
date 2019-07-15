/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
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
import org.bdp4j.util.MCD;

/**
 * Build a weka dataset
 *
 * @author María Novo
 */
public class Dataset implements Serializable, Cloneable {

    /**
     * Combines columns by summing (used for frequency/count values)
     */
    public static final CombineOperator COMBINE_SUM = new CombineOperator() {
        @Override
        public Double combine(Double a, Double b) {
            return a + b;
        }
    };

    /**
     * Combines columns by OR (used for binary representation)
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
     * The default value for the instances
     */
    private Instances dataset = null;

    /**
     * Default constructor, creates a new Dataset from instances
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
     * @param attributes The attribute list of instances
     * @param capacity The initial capacity of the instances
     */
    public Dataset(String name, ArrayList<Attribute> attributes, int capacity) {
        this.dataset = new Instances(name, attributes, capacity);
        
    }

    /**
     * Default constructor, creates a new Dataset
     *
     * @param name The name of the relation
     * @param attributes The attribute list of instances
     * @param capacity The initial capacity of the instances
     * @param outputFile The output file name, only in case you can export
     * instances to an output file.
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
     * Returns the number of attributes in instances
     *
     * @return the number of attributes in instances
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
     * Get an Instances from instances
     *
     * @return an Instances from instances
     */
    public weka.core.Instances getWekaDataset() {
        return new Instances(dataset);
    }

    /**
     * Print the instances content using Standard output
     */
    public void printLine() {
        dataset.stream().forEach(System.out::println);
    }

    /**
     * Generates a CSV with the instances contents. The CSV will be saved in the
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
     * Generates a CSV with instances content.
     *
     * @param transformersList The list of transformers
     * @param file The destination file
     * @return The ARFF content
     */
    public String generateARFFWithComments(Map<String, Transformer> transformersList, String file) {
        String comments = "";
        if (transformersList != null) {
            comments = getComments(transformersList);
        }
        // Generate 
        Instances wekaDataset = this.getWekaDataset();
        if (file.length() == 0) {
            file = "WEKADatasetWithComments.arff";
        }
        try (OutputStream outputStream = new FileOutputStream(new File(file))) {
            
            ArffSaver saver = new ArffSaver();
            saver.setInstances(wekaDataset);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outputStream));
            if (!comments.isEmpty()) {
                bw.write(comments + "\n");
            }
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
     * Get the attributes list of instances
     *
     * @return A list with the attributes of instances
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
     * Get the instance list of instances
     *
     * @return A list with the instances of instances
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
            if (!oldValue.equals("") || !newValue.equals("")) {
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
                    }
                } catch (NullPointerException ex) {
                    logger.warn(" Attribute name doesn't exist. " + ex.getMessage());
                }
            }
            if (listAttributeName.size() > 0) {
                deleteAttributeColumns(listAttributeName);
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
                Attribute attName = instances.attribute(attributeName);
                if (attName != null) {
                    int attPosition = instances.attribute(attributeName).index();
                    if (attPosition >= 0) {
                        instances.deleteAttributeAt(attPosition);
                    }
                }
            } catch (NullPointerException ex) {
                logger.warn(Dataset.class.getClass().getName() + ". Attribute >>" + attributeName + "<< doesn't exist. ");
            }
        }
        return this;
    }

    /**
     * Join attribute columns
     *
     * @param listAttributeNameToJoin The name of colums that should be joined
     * @param newAttributeName The name of the new column
     * @param op Operator that indicates the type of operation to do to combine
     * columns
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

    /**
     * Insert a column to the instances (inserted before a certain position)
     *
     * @param columnName The name of the column
     * @param columnType The type of the column
     * @param defaultValue The default values for the column included
     * @param position The index where the new column will be inserted (0 upto
     * the number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnAt(String columnName, Class<?> columnType, Object defaultValue, int position) {
        boolean isStringType = String.class.equals(columnType);
        boolean isEnum = Enum.class.equals(columnType);
        
        if (!isStringType && !isEnum && !Number.class.isAssignableFrom(columnType)) {
            logger.error("[INSERT COLUMN AT] Column type must be a String or a Number type");
            return false;
        } else if (isStringType && defaultValue != null && !(defaultValue instanceof String)) {
            logger.error("[INSERT COLUMN AT] Default value must have the column's type");
            return false;
        } else if (!isStringType && !isEnum && !(defaultValue instanceof Number)) {
            logger.error("[INSERT COLUMN AT] Default value must have the column's type");
            return false;
        }
        
        try {
            ArrayList<Attribute> attributes = new ArrayList<>();
            
            Enumeration<Attribute> attrEnum = this.dataset.enumerateAttributes();
            while (attrEnum.hasMoreElements()) {
                attributes.add(attrEnum.nextElement());
            }
            Attribute newAttribute = isStringType ? new Attribute(columnName, true) : (isEnum ? new Attribute(columnName, (List<String>) defaultValue) : new Attribute(columnName));
            
            if (!attributes.contains(newAttribute)) {
                attributes.add(position, newAttribute);
                Instances newDataset = new Instances("dataset", attributes, 0);
                this.dataset.forEach((instance) -> {
                    newDataset.add(new DenseInstance(attributes.size()));
                    Instance newInstance = newDataset.lastInstance();
                    
                    int indexOffset = 0;
                    for (Attribute attribute : attributes) {
                        if (attribute == newAttribute) {
                            
                            indexOffset = 1;
                            if (isStringType) {
                                newInstance.setValue(attribute, (String) defaultValue);
                            } else if (isEnum) {
                                newInstance.setValue(attribute, ((List<String>) defaultValue).get(0));
                            } else {
                                newInstance.setValue(attribute, ((Number) defaultValue).doubleValue());
                            }
                        } else {
                            newInstance.setValue(attribute, instance.value(attribute.index() - indexOffset));
                        }
                    }
                });
                
                this.dataset = newDataset;
            }            
            return true;
        } catch (Exception ex) {
            logger.error("[INSERT COLUMN AT] " + ex.getMessage());
            return false;
        }
    }

    /**
     * Insert a column to the instances (inserted before a certain position)
     *
     * @param column The column information
     * @param position The index where the new column will be inserted (0 upto
     * the number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnAt(ColumnDefinition column, int position) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        
        try {
            Enumeration<Attribute> attrEnum = this.dataset.enumerateAttributes();
            while (attrEnum.hasMoreElements()) {
                attributes.add(attrEnum.nextElement());
            }
            
            Attribute newAttribute = column.isStringType()
                    ? new Attribute(column.getColumnName(), true)
                    : new Attribute(column.getColumnName());
            attributes.add(position, newAttribute);
            
            Instances newDataset = new Instances("dataset", attributes, 0);
            
            for (Instance instance : this.dataset) {
                newDataset.add(new DenseInstance(attributes.size()));
                Instance newInstance = newDataset.lastInstance();
                
                int indexOffset = 0;
                for (Attribute attribute : attributes) {
                    if (attribute == newAttribute) {
                        indexOffset = 1;
                        if (column.isStringType()) {
                            newInstance.setValue(attribute, (String) column.getDefaultValue());
                        } else if (column.isEnumType()) {
                            newInstance.setValue(attribute, ((List<String>) column.getDefaultValue()).get(0));
                        } else {
                            newInstance.setValue(attribute, ((Number) column.getDefaultValue()).doubleValue());
                        }
                    } else {
                        newInstance.setValue(attribute, instance.value(attribute.index() - indexOffset));
                    }
                }
            }
            this.dataset = newDataset;
            return true;
        } catch (Exception ex) {
            logger.error("[INSERT COLUMN AT] " + ex.getMessage());
            return false;
        }
        
    }

    /**
     * Insert columns to the instances (inserted before a certain position)
     *
     * @param columnNames The name of the columns
     * @param columnTypes The type of the columns
     * @param defaultValues The default values for the columns included
     * @param position The index where the new columns will start to be inserted
     * (0 upto the number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnsAt(String columnNames[], Class<?> columnTypes[], Object defaultValues[], int position) {
        if (columnNames.length == columnTypes.length && columnTypes.length == defaultValues.length) {
            for (int i = 0; i < columnNames.length; i++) {
                if (!insertColumnAt(columnNames[i], columnTypes[i], defaultValues[i], position + i)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Insert columns to the instances (inserted before a certain position)
     *
     * @param columns The information for the columns included
     * @param position The index where the new columns will start to be inserted
     * (0 upto the number of columns - 1)
     * @return true if sucessfull, false otherwise
     */
    public boolean insertColumnsAt(ColumnDefinition[] columns, int position) {
        for (int i = 0; i < columns.length; i++) {
            if (!insertColumnAt(columns[i], position + i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add a column to the end of a Dataset
     *
     * @param columnName Column to add where columName stands for the name of
     * the column. Column add at the end.
     * @param columnType The type of the column
     * @param defaultValue Column value
     *
     * @return true if the column were sucessfully added, false otherwise
     */
    public boolean addColumn(String columnName, Class<?> columnType, Object defaultValue) {
        return insertColumnAt(columnName, columnType, defaultValue, this.numAttributes());
    }

    /**
     * Add a column to the end of a Dataset
     *
     * @param column Column to add where columName stands for the name of the
     * column. Column add at the end.
     *
     * @return true if the column were sucessfully added, false otherwise
     */
    public boolean addColumn(ColumnDefinition column) {
        return insertColumnAt(column, this.numAttributes());
    }

    /**
     * Add columns to the end of a Dataset
     *
     * @param columnNames List of columns to add where columNames stands for the
     * name of the column. Columns add at the end.
     * @param columnTypes List of types to each column.
     * @param defaultValues List of default values to each column
     *
     * @return true if the columns were sucessfully added, false otherwise
     */
    public boolean addColumns(String columnNames[], Class<?>[] columnTypes, Object[] defaultValues) {
        if (columnNames.length == columnTypes.length && columnTypes.length == defaultValues.length) {
            for (int i = 0; i < columnNames.length; i++) {
                if (!addColumn(columnNames[i], columnTypes[i], defaultValues[i])) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Add columns to the end of a Dataset
     *
     * @param columns List of columns to add where columNames stands for the
     * name of the column. Columns add at the end.
     *
     * @return true if the columns were sucessfully added, false otherwise
     */
    public boolean addColumns(ColumnDefinition[] columns) {
        for (ColumnDefinition column : columns) {
            if (!addColumn(column)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Add row to the instances
     *
     * @param values List of values of the row
     * @return true if the row could be added; false otherwise
     */
    public boolean addRow(Object[] values) {
        try {
            if (this.getAttributes().size() == values.length) {
                Instance instance = this.createDenseInstance();
                for (int i = 0; i < values.length; i++) {
                    if (this.getInstances().get(0).attribute(i).isNumeric()) {
                        instance.setValue(i, Double.parseDouble(values[i].toString()));
                    } else {
                        instance.setValue(i, values[i].toString());
                    }
                }
                return true;
            } else {
                logger.error("[ADD ROW] The number of attributes doesn't match with the number of instance values.");
                return false;
            }
        } catch (Exception ex) {
            logger.error("[ADD ROW] " + ex.getMessage());
            return false;
        }
    }

    /**
     * Add rows to the instances
     *
     * @param rowsToAdd List of rows to add
     * @return true if the rows could be added; false otherwise
     */
    public boolean addRows(Object[][] rowsToAdd) {
        if (rowsToAdd.length > 0) {
            for (Object[] row : rowsToAdd) {
                if (!addRow(row)) {
                    return false;
                }
            }
            return true;
        } else {
            logger.info("[ADD ROWS] There isn't rows to add.");
        }
        return false;
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

    /**
     * Split the dataset in many datasets (as many as indicated by parameter
     * outputDims).
     *
     * @param stratified Indicate if the dataset need to be stratified or not
     * @param outputDims Number of output datasets
     * @return An array of datasets
     */
    public Dataset[] split(boolean stratified, int... outputDims) {
        ArrayList<Attribute> attributes = new ArrayList<>();
        Instances instances = this.dataset;
        Enumeration<Attribute> attrEnum = instances.enumerateAttributes();
        Dataset retVal[] = new Dataset[outputDims.length];
        
        try {
            while (attrEnum.hasMoreElements()) {
                attributes.add(attrEnum.nextElement());
            }
            
            int mcd_res = MCD.mcd(outputDims);
            int instancesPerLoop[] = new int[outputDims.length];
            int loopSize = 0;
            for (int j = 0; j < outputDims.length; j++) {
                instancesPerLoop[j] = outputDims[j] / mcd_res;
                loopSize += instancesPerLoop[j];
            }
            
            for (int j = 0; j < outputDims.length; j++) {
                retVal[j] = new Dataset("dataset_" + j, attributes, 0);
            }
            
            if (stratified) {
                int att_size = (attributes.size() > 0 ? attributes.size() - 1 : 0);
                instances.sort(att_size);
            }
            
            for (int i = 0; i < instances.size(); i++) {
                int posInLoop = i % loopSize;
                int sum = 0;
                for (int k = 0; k < instancesPerLoop.length; k++) {
                    sum += instancesPerLoop[k];
                    if (posInLoop < sum) {
                        retVal[k].dataset.add(instances.get(i));
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("[SPLIT] " + ex.getMessage());
        }
        return retVal;
    }

    /**
     * Match test dataset attributes with training dataset attributes
     *
     * @param training Training dataset
     * @return Test dataset that contains only the attributes that match with
     * training dataset
     */
    public Dataset match(Dataset training) {
        
        List<String> attTest = this.getAttributes();
        List<String> attTraining = training.getAttributes();
        List<String> columnsToDelete = new ArrayList<>();
        try {
            for (String att : attTest) {
                if (!attTraining.contains(att)) {
                    columnsToDelete.add(att);
                }
            }
            if (columnsToDelete.size() > 0) {
                this.deleteAttributeColumns(columnsToDelete);
            }
        } catch (Exception ex) {
            logger.warn("[MATCH] " + ex.getMessage());
        }
        return this;
    }
}
