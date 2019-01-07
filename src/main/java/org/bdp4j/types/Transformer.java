/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

/**
 * To transform columns of a dataset into double representation suitable for using 
 * with classifiers.
 * @author María Novo
 */
public abstract class Transformer {

    /**
     * Transform the data
     * @param input Data to transform
     * @return The double representation of the input
     */
    public abstract double transform(Object input);
    
    /**
     * Get a String who contents the meaning of the transformated values
     * 
     * @return String who contents the meaning of the transformated values
     */
    public abstract String getTransformerListValues();
}
