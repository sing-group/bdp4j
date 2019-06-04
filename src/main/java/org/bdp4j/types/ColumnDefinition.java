/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

/**
 *
 * @author María Novo
 */
public class ColumnDefinition {

    private String columnName;
    private Class<?> columnType;
    private Object defaultValue;

    public static ColumnDefinition stringColumn(String columnName, String value) {
        return new ColumnDefinition(columnName, value);
    }

    public static ColumnDefinition doubleColumn(String columnName, Double value) {
        return new ColumnDefinition(columnName, value);
    }

    public ColumnDefinition(String columnName, Object defaultValue) {
        this(columnName, defaultValue.getClass(), defaultValue);
    }

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

    public final boolean isStringType() {
        return String.class.equals(columnType);
    }

    public String getColumnName() {
        return columnName;
    }

    public Class<?> getColumnType() {
        return columnType;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

}
