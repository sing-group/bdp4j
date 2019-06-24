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

import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatasetStore {

    private static final Logger logger = LogManager.getLogger(DatasetStore.class);

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
