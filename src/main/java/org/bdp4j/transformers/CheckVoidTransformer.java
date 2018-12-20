/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers;

import org.bdp4j.types.Transformer;

/**
 * Trasform an input from String to Double, that represents if this input is
 * empty or not.
 *
 * @author María Novo
 */
public class CheckVoidTransformer extends Transformer<String> {

    private String transformerListValues;
    /**
     * Trasform an input from String to Double, that represents if this input is
     * empty or not.
     *
     * @param input A string to transform in Double
     * @return A double value that represents a void or not void value
     */
    @Override
    public double transform(Object input) {
        try {
            return ((input == null || input.equals("null")) ? 0 : 1);
        } catch (NullPointerException ex) {
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
