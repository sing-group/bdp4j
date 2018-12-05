/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.ml;

import java.io.File;
<<<<<<< refs/remotes/origin/master:src/main/java/org/bdp4j/ml/DatasetFromFile.java
//import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.StringReader;
//import java.io.Writer;
=======
import java.io.FileReader;
import java.io.IOException;
>>>>>>> Changes in CSVDataset files:src/main/java/org/bdp4j/ml/CSVDatasetFromFile.java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
<<<<<<< refs/remotes/origin/master:src/main/java/org/bdp4j/ml/DatasetFromFile.java
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
=======
>>>>>>> Changes in CSVDataset files:src/main/java/org/bdp4j/ml/CSVDatasetFromFile.java
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.util.Pair;
import org.bdp4j.types.CSVDataset;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.DateIdentifier;
import org.bdp4j.util.SubClassParameterTypeIdentificator;

/**
 *
 * Generate Dataset from file. This dataset will contain only columns with a
 * float value. This class allows to use transformers to convert a non float
 * value in float value.
 *
 * @author María Novo
 */
public class CSVDatasetFromFile {

    /**
     * For logging purposes
     */
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CSVDatasetFromFile.class);

    /**
     * The filepath/filename to load
     */
    String filePath;
    /**
     * The list of transformers. A transformer is a class used to transform a
     * non double value in double value.
     *
     */
    Map<String, Transformer<? super Object>> transformersList;

    /**
     * Default constructor
     */
    private CSVDatasetFromFile() {
    }

    /**
     * Create a DatasetFromFile object from a file path
     *
     * @param filePath: The file path
     */
    public CSVDatasetFromFile(String filePath) {
        this.filePath = filePath;
        this.transformersList = new HashMap<>();
    }

    /**
     * Create a DatasetFromFile object from a filepath/filename and a
     * transformers list
     *
     * @param filePath The filepath/filename
     * @param transformersList The list of transformers.
     */
<<<<<<< refs/remotes/origin/master:src/main/java/org/bdp4j/ml/DatasetFromFile.java
    public DatasetFromFile(String filePath, Map<String, Transformer<? super Object>> transformersList) {
=======
    public CSVDatasetFromFile(String filePath, Map<String, Transformer> transformersList) {
>>>>>>> Changes in CSVDataset files:src/main/java/org/bdp4j/ml/CSVDatasetFromFile.java
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
    public void setTransformersList(Map<String, Transformer<? super Object>> transformersList) {
        this.transformersList = transformersList;
    }

    /**
     * Get the transformersList
     *
     * @return the transformersList
     */
    public Map<String, Transformer<? super Object>> getTransformersList() {
        return this.transformersList;
    }

    /**
     * This method load the file and generates a Dataset, applying , if exists,
     * the transformers list. The dataset will only contain double values.
     */
    public void loadFile() {
        try {

            try (
                    FileReader reader = new FileReader(new File(this.filePath));
                    FileReader dsReader = new FileReader(new File(this.filePath))) {
<<<<<<< refs/remotes/origin/master:src/main/java/org/bdp4j/ml/DatasetFromFile.java
                Pair<String, String> pair;
=======
                Pair pair;
                Pair newPair;
>>>>>>> Changes in CSVDataset files:src/main/java/org/bdp4j/ml/CSVDatasetFromFile.java
                //List to save the pair <columnName, datatype>
                List<Pair<String, String>> columnTypes = new ArrayList<Pair<String, String>>();
                List<String> headers = new ArrayList<>();
                Map<String, Integer> indexColumnTypes = new HashMap<>();
                // Used to known when all column types are identified.
                Set<String> detectedTypes = new HashSet<>();
                CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withQuote('"');
                Iterable<CSVRecord> records = csvFormat.parse(reader);
                long lineNumber;
                String field;

                // Loop to detect type of each file column. 
                // Create a columnTypes list, with the name of the column and the data type
                for (CSVRecord record : records) {
                    lineNumber = record.getRecordNumber();
                    if (lineNumber == 1) {
                        for (int index = 0; index < record.size(); index++) {
                            if (record.get(index) != null && !record.get(index).equals("")) {
                                headers.add(record.get(index));
                            }
<<<<<<< refs/remotes/origin/master:src/main/java/org/bdp4j/ml/DatasetFromFile.java
                        } else {
                            String type;
                            for (int index = 0; index < record.size(); index++) {
                                field = record.get(index);
                                type = "String";
                                if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                    if (!detectedTypes.contains(headers.get(index))) {
                                        // Check if the field is Double                            
                                        try {
                                            Double.parseDouble(field);
                                            type = "Double";
                                        } catch (Exception ex) {
                                            if (ex.getClass().getName().equals("java.lang.NumberFormatException")) {
                                            }
                                        }
                                        // Check if the field is Date                            
                                        try {
                                            if (DateIdentifier.getDefault().checkDate(field) != null) {
                                                type = "Date";
                                            }
                                        } catch (Exception ex) {
                                            if (ex.getClass().getName().equals("java.text.ParseException")) {
                                            }
                                        }
                                        // Create a Map to an easier generation of dataset
                                        if (!type.equals("")) {
                                            detectedTypes.add(headers.get(index));
                                            pair = new Pair<String, String> (headers.get(index), type);
                                            columnTypes.add(pair);
                                        }
=======
                        }
                    } else {
                        String type;
                        for (int index = 0; index < record.size(); index++) {
                            field = record.get(index);
                            type = "String";
                            if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                // Check if the field is Double                            
                                try {
                                    Double.parseDouble(field);
                                    type = "Double";
                                } catch (Exception ex) {
                                    if (ex.getClass().getName().equals("java.lang.NumberFormatException")) {
                                    }
                                }
                                // Check if the field is Date                            
                                try {
                                    if (DateIdentifier.getDefault().checkDate(field) != null) {
                                        type = "Date";
                                    }
                                } catch (Exception ex) {
                                    if (ex.getClass().getName().equals("java.text.ParseException")) {
                                    }
                                }
                                if (detectedTypes.contains(headers.get(index))) {
                                    int columnTypeIndex = indexColumnTypes.get(headers.get(index));
                                    pair = columnTypes.get(columnTypeIndex);
                                    if (pair.getObj2() != type) {
                                        columnTypes.remove(columnTypeIndex);
                                        newPair = new Pair(headers.get(index), "String");
                                        columnTypes.add(columnTypeIndex, newPair);
                                    }
                                } else {
                                    // Create a Map to an easier generation of dataset
                                    if (!type.equals("")) {
                                        detectedTypes.add(headers.get(index));
                                        pair = new Pair(headers.get(index), type);
                                        columnTypes.add(pair);
                                        indexColumnTypes.put(headers.get(index), columnTypes.indexOf(pair));
>>>>>>> Changes in CSVDataset files:src/main/java/org/bdp4j/ml/CSVDatasetFromFile.java
                                    }
                                }
                            }
                        }
                    }
                }

                // Get transformes which parameter type is not Double
                Set<String> noDoubleTransformers = new HashSet<>();
                if (transformersList.size() > 0) {
                    for (Map.Entry<String, Transformer<? super Object>> entry : transformersList.entrySet()) {
                        String key = entry.getKey();
                        Transformer<? super Object> value = entry.getValue();
                        if (!SubClassParameterTypeIdentificator.findSubClassParameterType(value, Transformer.class, 0).getName().equals("Double")) {
                            noDoubleTransformers.add(key);
                        }
                    }
                }

                // Get attribute list to generate CSVDataset. This list will contain the columns to add to the dataset.
                List<String> attributes = new ArrayList<>();
                if (!columnTypes.isEmpty()) {
                    for (Iterator<Pair<String, String>> iterator = columnTypes.iterator(); iterator.hasNext();) {
                        Pair<String, String> next = iterator.next();
                        if ((next.getObj2().equals("Double") || noDoubleTransformers.contains(next.getObj1().toString())) && !attributes.contains(next.getObj1().toString())) {
                            attributes.add(next.getObj1().toString());
                        }
                    }
                }

                // Generate CSVDataset
                CSVDataset dataset = new CSVDataset(attributes);
                records = csvFormat.parse(dsReader);
                List<String> instanceIds = new ArrayList<>();
                for (CSVRecord record : records) {
                    lineNumber = record.getRecordNumber();
                    if (lineNumber > 1) {
                        double[] instanceValues = new double[attributes.size()];
                        int indInstance = 0;
                        for (int index = 0; index < headers.size(); index++) {
                            field = record.get(index);
                            //Save instance id
                            if (index == 0) {
                                instanceIds.add(field);
                            }
                            if (attributes.contains(headers.get(index))) {
                                Transformer<? super Object> t;
                                try {
                                    if ((t = transformersList.get(headers.get(index))) != null) {
                                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                            try {
                                                instanceValues[indInstance] = t.transform(field);
                                            } catch (Exception ex) {
                                                instanceValues[indInstance] = 0d;
                                                logger.error(ex.getMessage());
                                            }
                                        } else {
                                            instanceValues[indInstance] = 0d;
                                        }
                                    } else {
                                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                            try {
                                                instanceValues[indInstance] = Double.parseDouble(field);
                                            } catch (NumberFormatException ex) {
                                                instanceValues[indInstance] = 0d;
                                                logger.error(ex.getMessage());
                                            }

                                        } else {
                                            instanceValues[indInstance] = 0d;
                                        }
                                    }
                                } catch (Exception ex) {
                                    logger.error(ex.getMessage());
                                    ex.printStackTrace();
                                }
                                indInstance++;
                            }
                        }
                        Instance instance = new DenseInstance(instanceValues);
                        dataset.add(instance);
                    }
                }
                dataset.setInstanceIds(instanceIds);
/*                
                CSVDataset sortedDataset =  new CSVDataset(attributes);
                CSVDatasetUtils utils = new CSVDatasetUtils();
                sortedDataset = utils.sort(dataset);

                try {
                    FileHandler.exportDataset(dataset, new File("data.csv"), false, ";");
                    Dataset data = FileHandler.loadDataset(new File("data.csv"), 0, ";");
                    // Contruct a KNN classifier that uses 5 neighbors to make a decision. 
                    Classifier knn = new KNearestNeighbors(5);
                   
                    knn.buildClassifier(data);
                    Dataset dataForClassification = FileHandler.loadDataset(new File("data.csv"), 0, ";");

                    Map<Object, PerformanceMeasure> pm = EvaluateDataset.testDataset(knn, dataForClassification);
                    for (Object o : pm.keySet()) {
                        System.out.println(o + ": " + pm.get(o).getAccuracy());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
*/
                //---------------------------------------------------------------------------
                // Se genera un fichero csv donde se añade el contenido del dataset
                //---------------------------------------------------------------------------
                dataset.generateCSV();

                //---------------------------------------------------------------------------
                // Se imprime el dataset
                //---------------------------------------------------------------------------
                System.out.println("-------------BEGIN DATASET-----------------------");
                dataset.stream().forEach(System.out::println);
                System.out.println("-------------END DATASET-----------------------");
                //---------------------------------------------------------------------------
                // Se imprime el dataset
                //---------------------------------------------------------------------------
//                System.out.println("-------------BEGIN sortedDataset-----------------------");
//                sortedDataset.stream().forEach(System.out::println);
//                System.out.println("-------------END sortedDataset-----------------------");

            } catch (IOException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
}
