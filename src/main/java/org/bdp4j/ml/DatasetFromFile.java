/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.ml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Dataset;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.util.Pair;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.DateIdentifier;
import org.bdp4j.util.SubClassParameterTypeIdentificator;

import weka.core.Instance;
import weka.core.Attribute;

/**
 *
 * Generate Dataset from file. This dataset will contain only columns with a
 * float value. This class allows to use transformers to convert a non float
 * value in float value.
 *
 * @author María Novo
 */
public class DatasetFromFile {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(DatasetFromFile.class);

    /**
     * The filepath/filename to load
     */
    String filePath;
    /**
     * The list of transformers. A transformer is a class used to transform a
     * non double value in double value.
     *
     */
    Map<String, Transformer> transformersList;
    //Map<String, Transformer> transformersList;

    /**
     * Create a CSVDatasetFromFile object from a file path
     *
     * @param filePath: The file path
     */
    public DatasetFromFile(String filePath) {
        this.filePath = filePath;
        this.transformersList = new HashMap<>();
    }

    /**
     * Create a CSVDatasetFromFile object from a filepath/filename and a
     * transformers list
     *
     * @param filePath The filepath/filename
     * @param transformersList The list of transformers.
     */
    public DatasetFromFile(String filePath, Map<String, Transformer> transformersList) {
        //public DatasetFromFile(String filePath, Map<String, Transformer> transformersList) {
        this.filePath = filePath;
        this.transformersList = transformersList;
    }

    /**
     * Set the file path to load
     *
     * @param filePath The filepath/filename to load
     */
    @PipeParameter(name = "filePath", description = "The filepath/filename to load", defaultValue = "")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Get the filepath/filename to load
     *
     * @return the filepath/filename to load
     */
    public String getFilePath() {
        return this.filePath;
    }

    /**
     * Set the transformers list
     *
     * @param transformersList The list of transformers.
     */
    @PipeParameter(name = "transformersList", description = "The list of transformers", defaultValue = "")
    public void setTransformersList(Map<String, Transformer> transformersList) {
        this.transformersList = transformersList;
    }

    /**
     * Get the transformersList
     *
     * @return the transformersList
     */
    public Map<String, Transformer> getTransformersList() {
        //public Map<String, Transformer> getTransformersList() {
        return this.transformersList;
    }
    
    private String identifyType(String value) {
        // Check if the field is Double                            
        try {
            Double.parseDouble(value);
            return "Double";
        } catch (Exception ex) {
            if (ex.getClass().getName().equals("java.lang.NumberFormatException")) {
            }
        }
        // Check if the field is Date                            
        try {
            if (DateIdentifier.getDefault().checkDate(value) != null) {
                return "Date";
            }
        } catch (Exception ex) {
            if (ex.getClass().getName().equals("java.text.ParseException")) {
            }
        }
        return "String";
    }

    /**
     * This method load the file and generates a Dataset, applying , if exists,
     * the transformers list. The dataset will only contain double values.
     * 
     * @return A processed Dataset
     */
    public Dataset loadFile() {
        Dataset dataset = null;
        try (
            FileReader reader = new FileReader(new File(this.filePath));
            FileReader dsReader = new FileReader(new File(this.filePath))) {
            Pair<String, String> pair;
            Pair<String, String> newPair;
            //List to save the pair <columnName, datatype>
            List<Pair<String, String>> columnTypes = new ArrayList<>();
            List<String> headers = new ArrayList<>();
            Map<String, Integer> indexColumnTypes = new HashMap<>();
            // Used to known when all column types are identified.
            Set<String> detectedTypes = new HashSet<>();
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withQuote('"');
            Iterable<CSVRecord> records = csvFormat.parse(reader);
            long lineNumber;
            String field;

            // Loop to detect type of each file column. 
            Predicate<String> isDetectedColumnType = name -> columnTypes.stream().anyMatch(header -> header.getObj1().equals(name));
            // Create a columnTypes list, with the name of the column and the data type
            for (CSVRecord record : records) {
                lineNumber = record.getRecordNumber();
                if (lineNumber == 1) {
                    for (int index = 0; index < record.size(); index++) {
                        if (record.get(index) != null && !record.get(index).equals("")) {
                            headers.add(record.get(index));
                        }
                    }
                } else {
                    String type;
                    for (int index = 0; index < record.size(); index++) {
                        field = record.get(index);
                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                            type = identifyType(field);
                            if (detectedTypes.contains(headers.get(index))) {
                                int columnTypeIndex = indexColumnTypes.get(headers.get(index));
                                pair = columnTypes.get(columnTypeIndex);
                                if (!pair.getObj2().equals(type)) {
                                    columnTypes.remove(columnTypeIndex);
                                    newPair = new Pair<>(headers.get(index), "String");
                                    columnTypes.add(columnTypeIndex, newPair);
                                }
                            } else {
                                // Create a Map to an easier generation of dataset
                                detectedTypes.add(headers.get(index));
                                if (isDetectedColumnType.test("target")) {
                                    // Target field always has to be the last one
                                    int lastColumnTypesPosition = columnTypes.size() - 1;
                                    Pair<String, String> targetPair = columnTypes.get(lastColumnTypesPosition);
                                    columnTypes.remove(lastColumnTypesPosition);
                                    pair = new Pair<>(headers.get(index), type);
                                    columnTypes.add(pair);
                                    indexColumnTypes.put(headers.get(index), columnTypes.indexOf(pair));
                                    columnTypes.add(targetPair);
                                    indexColumnTypes.put("target", columnTypes.indexOf(pair));
                                } else {
                                    pair = new Pair<>(headers.get(index), type);
                                    columnTypes.add(pair);
                                    indexColumnTypes.put(headers.get(index), columnTypes.indexOf(pair));
                                }
                            }
                        }
                    }
                }
            }

            // Get transformes which parameter type is not Double
            Set<String> noDoubleTransformers = new HashSet<>();
            if (transformersList.size() > 0) {
                for (Map.Entry<String, Transformer> entry : transformersList.entrySet()) {
                    String key = entry.getKey();
                    Transformer value = entry.getValue();
                    if (!SubClassParameterTypeIdentificator.findSubClassParameterType(value, Transformer.class, 0).getName().equals("Double")) {
                        noDoubleTransformers.add(key);
                    }
                }
            }

            // Get attribute list to generate Dataset. This list will contain the columns to add to the dataset.
            ArrayList<Attribute> attributes = new ArrayList<>();
            Predicate<String> isAttribute = name -> attributes.stream()
                    .anyMatch(attribute -> attribute.name().equals(name));
            
            attributes.add(new Attribute("id", true));
            if (!columnTypes.isEmpty()) {
                for (Pair<String, String> next : columnTypes) {
                    final String type = next.getObj2();
                    final String header = next.getObj1();
                    
                    if ((type.equals("Double") || noDoubleTransformers.contains(header)) && !isAttribute.test(header)) {
                        attributes.add(new Attribute(header));
                    }
                }
            }

            // Generate Dataset
            dataset = new Dataset("dataset", attributes, 0);
            
            records = csvFormat.parse(dsReader);
            
            for (CSVRecord record : records) {
                lineNumber = record.getRecordNumber();
                if (lineNumber > 1) {
                    Instance instance = dataset.createDenseInstance();
                    int indInstance = 0;
                    for (int index = 0; index < headers.size(); index++) {
                        field = record.get(index);
                        
                        if (isAttribute.test(headers.get(index))) {
                            //System.out.println(headers.get(index));
                            Transformer t;
                            try {
                                if (index == 0) {
                                    instance.setValue(indInstance, field);
                                } else {
                                    if ((t = transformersList.get(headers.get(index))) != null) {
                                        if (field != null && !field.isEmpty() && !field.equals("null") && !field.equals("") && !field.equals(" ")) {
                                            try {
                                                instance.setValue(indInstance, t.transform(field));
                                            } catch (Exception ex) {
                                                instance.setValue(indInstance, 0d);
                                                logger.error(ex.getMessage());
                                            }
                                        } else {
                                            instance.setValue(indInstance, 0d);
                                        }
                                    } else {
                                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                            try {
                                                
                                                instance.setValue(indInstance, Float.parseFloat(field));
                                            } catch (NumberFormatException ex) {
                                                
                                                instance.setValue(indInstance, 0d);
                                                logger.error(ex.getMessage());
                                            }
                                        } else {
                                            instance.setValue(indInstance, 0d);
                                        }
                                    }
                                }
                            } catch (Exception ex) {
                                logger.error(ex.getMessage());
                                ex.printStackTrace();
                            }
                            indInstance++;
                        }
                    }
                }
            }

            //---------------------------------------------------------------------------
            // Se genera un fichero csv donde se añade el contenido del dataset
            //---------------------------------------------------------------------------
            dataset.generateCSV();
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return dataset;
    }
}
