/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers;

import org.bdp4j.types.Transformer;

/**
 * Trasform an input from String to Double, that represents the input lenght.
 *
 * @author María Novo

 */
public class ComputeStringLenghtTransformer extends Transformer<String> {

    private String transformerListValues;

    /**
     * Trasform an input from String to Double, that represents the input
     * lenght.
     *
     * @param input A string to transform in Double
     * @return A double value that represents the string length
     */
    @Override
    public double transform(Object input) {
        return new Double(((String)input).length());
    }
    
    
    @Override
    public String getTransformerListValues() {
        return transformerListValues;
    }
}
