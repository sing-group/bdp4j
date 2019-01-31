/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

/**
 * Represents a list of dataset 
 *
 * @author María Novo
 */
public enum DatasetType {
    STRING("String"),
    DOUBLE("Double"),
    DATE("Date");

    /**
     * The description of the DatasetType
     */
    private final String desc;

    /**
     * Creates the enum instance
     *
     * @param desc The full description of the constant
     */
    private DatasetType(final String desc) {
        this.desc = desc;
    }

    /**
     * Find the description of the constant
     *
     * @return the description of the constant
     */
    public String getDesc() {
        return this.desc;
    }

    /**
     * Builds a string representation of the constant
     *
     * @return the string representation of the constant
     */
    @Override
    public String toString() {
        return desc;
    }
}
