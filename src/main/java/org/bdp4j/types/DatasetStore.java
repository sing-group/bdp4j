/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author María Novo
 */
public class DatasetStore {

    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(DatasetStore.class);

    /**
     * The information storage for the dataset. Only a Hashset of synsetsId
     * is required
     */
    private Dataset dataset;

    /**
     * A instance of the datasetStore to implement a singleton pattern
     */
    private static DatasetStore datasetStore = null;

    /**
     * The default constructor
     */
    private DatasetStore() {
        dataset = new Dataset("dataset",  new ArrayList<>(), 0);
    }

    /**
     * Retrieve the Stored Dataset
     *
     * @return The default Dataset for the system
     */
    public static DatasetStore getDatasetStore() {
        if (datasetStore == null) {
            datasetStore = new DatasetStore();
        }
        return datasetStore;
    }
    
    /**
     * Add a column to dataset
     * @param columnName Name of the column
     * @param columnType Type of the column
     * @param defaultValue Default value to the column
     * @return  True if column has been properly add, false otherwise
     */
    public boolean addColumn(String columnName, Class<?> columnType, Object defaultValue){
        return dataset.addColumn(columnName, columnType, defaultValue);
    }
    
    /**
     * Add a row to the dataset
     * @param values List of values of the row
     * @return  True if row has been properly add, false otherwise
     */
    public boolean addRow(Object[] values) {
        return dataset.addRow(values);
    }
    
    /**
     * Get the dataset
     * @return The dataset stored
     */
    public Dataset getDataset(){
        return dataset;
    }
    
    /**
     * Get the size of dataset
     * @return Size of dataset
     */
    public int size(){
        return datasetStore.size();
    }
  
}
