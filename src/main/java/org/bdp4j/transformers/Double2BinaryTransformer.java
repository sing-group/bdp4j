/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers;

import org.bdp4j.types.Transformer;

/**
 * Trasform an input from String, that represents a Data to Double
 *
 * @author María Novo
 */
public class Double2BinaryTransformer extends Transformer<Object> {

    private String transformerListValues;

    /**
     * Transform an input, that represents a Date to Double
     *
     * @param input A Double to transform in binary
     * @return  A Double value that represents a binary value
     */
    @Override
    public double transform(Object input) {
        if (input != null && !input.equals("null")) {
            try {
                Double value = Double.parseDouble(input.toString());
                if (value>0)
                    return 1;
                else return 0;
            } catch (NullPointerException ex) {
                return 0;
            }
        } else {
            return 0;
        }
    }
    
    /**
     * Get a String who contents the meaning of the transformated values
     * 
     * @return String who contents the meaning of the transformated values
     */
    @Override
    public String getTransformerListValues() {
        return transformerListValues;
    }
}