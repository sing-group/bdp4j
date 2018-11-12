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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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

            try (FileReader reader = new FileReader(new File(this.filePath))) {
                Pair pair;
                //List to save the pair <columnName, datatype>
                List<Pair<String, String>> columnTypes = new ArrayList<Pair<String, String>>();
                //Map to save index of each column
                //Map<String, Integer> indiceColumnTypes = new HashMap<>();
                String[] headers = null;
                Set<String> detectedTypes = new HashSet<>();
                Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(reader);
                long lineNumber;
                String[] line;
                String field;
                // Loop to detect type of each file column
                for (CSVRecord record : records) {
                    line = record.get(0).split(";");
                    if (detectedTypes.size() < line.length) {
                        lineNumber = record.getRecordNumber();
                        if (lineNumber == 1) {
                            headers = line;
                        } else {
                            String type;
                            for (int index = 0; index < line.length; index++) {
                                type = "String";
                                field = line[index];
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
                                    // Create a Map to an easier generation of dataset
                                    if (!type.equals("")) {
                                        if (!detectedTypes.contains(headers[index])) {
                                            detectedTypes.add(headers[index]);
                                        }
                                        pair = new Pair(headers[index], type);
                                        columnTypes.add(pair);
                                        //indiceColumnTypes.put(headers[index], columnTypes.indexOf(pair));
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
                        if (next.getObj2().equals("Double") || noDoubleTransformers.contains(next.getObj1().toString())) {
                            attributes.add(next.getObj1().toString());
                        }
                    }
                }

                CSVDataset dataset = new CSVDataset(attributes);
                records = CSVFormat.EXCEL.parse(reader);
                List<String> instanceIds = new ArrayList<>();
                for (CSVRecord record : records) {
                    lineNumber = record.getRecordNumber();
                    line = record.get(0).split(";");
                    if (lineNumber > 1) {
                        Instance instance = null;
                        for (int index = 0; index < line.length; index++) {
                            field = line[index];
                            instance = new DenseInstance(attributes.size());
                            //Save instance id
                            if (index == 0) {
                                instanceIds.add(field);
                            }
                            if (attributes.contains(headers[index])) {
                                Transformer t;
                                try {
                                    if ((t = transformersList.get(headers[index])) != null) {
                                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                            Double d = t.transform(field);
                                            instance.add(d);
                                            System.out.println(headers[index]+" | "+field +" --> "+d);
                                        } else {
                                            instance.add(0d);
                                            System.out.println("NO_1");
                                        }
                                    } else {
                                        if (field != null && !field.isEmpty() && !field.equals("") && !field.equals(" ")) {
                                            instance.add(Double.parseDouble(field));
                                        } else {
                                            instance.add(0d);
                                            System.out.println("NO_2");
                                        }
                                    }
                                } catch (Exception ex) {
                                    System.out.println("org.bdp4j.ml.DatasetFromFile.loadFile()" + ex.getMessage());
                                }
                            }
                        }
                        dataset.add(instance);

                    }
                }
                dataset.setInstanceIds(instanceIds);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {
            Logger.getLogger(DatasetFromFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
