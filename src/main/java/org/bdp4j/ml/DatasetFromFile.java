/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.ml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.bdp4j.util.Pair;
import org.bdp4j.types.CSVDataset;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.DateIdentifier;
import org.bdp4j.util.SubClassParameterTypeIdentificator;

/**
 *
 * @author María Novo
 */
public class DatasetFromFile {

    String filePath;
    Map<String, Transformer> transformersList;

    private DatasetFromFile() {
    }

    public DatasetFromFile(String filePath) {
        this.filePath = filePath;
        this.transformersList = new HashMap<>();
    }

    public DatasetFromFile(String filePath, Map<String, Transformer> transformersList) {
        this.filePath = filePath;
        this.transformersList = transformersList;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setTransformersList(Map<String, Transformer> transformersList) {
        this.transformersList = transformersList;
    }

    public Map<String, Transformer> getTransformersList() {
        return this.transformersList;
    }
//
//    private Transformer getTransformerValue(String key) {
//        if (transformersList.containsKey(key)) {
//            return transformersList.get(key);
//        } else {
//            return null;
//        }
//    }

    public void loadFile() {
        try {

            try (
                    FileReader reader = new FileReader(new File(this.filePath));
                    FileReader dsReader = new FileReader(new File(this.filePath))) {
                Pair pair;
                //List to save the pair <columnName, datatype>
                List<Pair<String, String>> columnTypes = new ArrayList<Pair<String, String>>();
                //Map to save index of each column
                //Map<String, Integer> indiceColumnTypes = new HashMap<>();
//                String[] headers = null;
                List<String> headers = new ArrayList<>();
                Set<String> detectedTypes = new HashSet<>();
                CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').withQuote('"');
                Iterable<CSVRecord> records = csvFormat.parse(reader);
                long lineNumber;
                String[] line;
                String field;

                // Loop to detect type of each file column
                for (CSVRecord record : records) {
                    // line = record.get(0).split(";");
                    if (detectedTypes.size() < record.size()) {
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
                                            pair = new Pair(headers.get(index), type);
                                            columnTypes.add(pair);
                                            //indiceColumnTypes.put(headers[index], columnTypes.indexOf(pair));
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }

                // Generate CSVDataset
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
                // Get attribute list to generate CSVDataset
                List<String> attributes = new ArrayList<>();
                if (!columnTypes.isEmpty()) {
                    for (Iterator<Pair<String, String>> iterator = columnTypes.iterator(); iterator.hasNext();) {
                        Pair next = iterator.next();
                        if ((next.getObj2().equals("Double") || noDoubleTransformers.contains(next.getObj1().toString())) && !attributes.contains(next.getObj1().toString())) {
                            attributes.add(next.getObj1().toString());

                        }
                    }
                }
                CSVDataset dataset = new CSVDataset(attributes);
                records = csvFormat.parse(dsReader);
                List<String> instanceIds = new ArrayList<>();
                for (CSVRecord record : records) {
                    lineNumber = record.getRecordNumber();
                    if (lineNumber > 1) {

                        // Instance instance = new DenseInstance(attributes.size());
                        double[] instanceValues = new double[attributes.size()];
                        int indInstance = 0;
                        for (int index = 0; index < headers.size(); index++) {
                            field = record.get(index);
                            //Save instance id
                            if (index == 0) {
                                instanceIds.add(field);
                            }
                            if (attributes.contains(headers.get(index))) {
                                Transformer t;
                                try {
                                    if ((t = transformersList.get(headers.get(index))) != null) {
                                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                            instanceValues[indInstance] = t.transform(field);
                                        } else {
                                            instanceValues[indInstance] = 0d;
                                        }
                                    } else {

                                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                            instanceValues[indInstance] = Double.parseDouble(field);

                                        } else {
                                            instanceValues[indInstance] = 0d;
                                        }
                                    }
                                } catch (Exception ex) {
                                    System.out.println("ERROR org.bdp4j.ml.DatasetFromFile.loadFile() " + ex.getMessage() + ">> index:" + index + ", getIndex: " + headers.get(index) + ", field: " + field);
                                    ex.printStackTrace();
                                }
                                indInstance++;
                            }

                        }
                        Instance instance = new DenseInstance(instanceValues);

                        dataset.add(instance);
                        //dataset.stream().forEach(System.out::println);
                    }
                }
                dataset.setInstanceIds(instanceIds);
                List<String> att = dataset.getAttributes();
                List<String>  insid= dataset.getInstanceIds();
                // Se genera un fichero dataset.txt donde se añade el contenido del dataset
                System.out.println("DATASET");
                System.out.println("------------------------------------");
                String csv = dataset.stream().map(Instance::toString).collect(Collectors.joining("\n"));

                try (Writer output = new OutputStreamWriter(new FileOutputStream("dataset.txt"))) {
                    output.write(csv);
                    output.flush();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                //dataset.stream().forEach(System.out::println);
                System.out.println("------------------------------------");
            } catch (IOException e) {
                System.out.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception ex) {
            Logger.getLogger("ERROR: " + DatasetFromFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
