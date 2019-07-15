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

/**
 * Definition of a column in a dataset (name, type and default value)
 * @author María Novo 
 */
public class ColumnDefinition {

    private String columnName;
    private Class<?> columnType;
    private Object defaultValue;

    /**
     * Creates an String column definition 
     * @param columnName The column name
     * @param value The value
     * @return The desired column definition
     */
    public static ColumnDefinition stringColumn(String columnName, String value) {
        return new ColumnDefinition(columnName, value);
    }

    /**
     * Creates an Double column definition with
     * @param columnName The column name
     * @param value The value
     * @return The desired column definition
     */

    public static ColumnDefinition doubleColumn(String columnName, Double value) {
        return new ColumnDefinition(columnName, value);
    }

    /**
     * Creates an Object column definition with
     * @param columnName The column name
     * @param defaultValue The value
     */

    public ColumnDefinition(String columnName, Object defaultValue) {
        this(columnName, defaultValue.getClass(), defaultValue);
    }

    /**
     * Creates a column definition with the values for all attributes
     * @param columnName The column Name
     * @param columnType The column type
     * @param defaultValue The default value
     */
    public ColumnDefinition(String columnName, Class<?> columnType, Object defaultValue) {
        boolean isStringType = String.class.equals(columnType);
       
        if (columnType != null && (!isStringType && !Number.class.isAssignableFrom(columnType))) {
            throw new IllegalArgumentException("Column type must be a String or a Number type");
        } else if (isStringType && defaultValue != null && !(defaultValue instanceof String)) {
            throw new IllegalArgumentException("Default value must have the column's type");
        } else if (!isStringType && !(defaultValue instanceof Number)) {
            throw new IllegalArgumentException("Default value must have the column's type");
        }

        this.columnName = columnName;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
    }

    /**
     * Indicates whether the columns is String or not
     * @return true if the column type is string
     */
    public final boolean isStringType() {
        return String.class.equals(columnType);
    }
    
     public final boolean isEnumType() {
        return Enum.class.equals(columnType);
    }

    /**
     * Returns the column name 
     * @return The column name
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Returns the type of the column
     * @return The type of the column
     */
    public Class<?> getColumnType() {
        return columnType;
    }

    /**
     * Returns the default value for the column
     * @return The default value for the column
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

}
