/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers;

import java.util.Date;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.DateIdentifier;

/**
 * Trasform an input from String, that represents a Data to Double
 *
 * @author María Novo
 */
public class Date2MillisTransformer extends Transformer<Object> {

    private String transformerListValues;

    /**
     * Transform an input, that represents a Date to Double
     *
     * @param input A string to transform in Double
     * @return A double value that represents a Date
     */
    @Override
    public double transform(Object input) {
       
        if (input != null && !input.equals("null")) {
            try {
                Date date = DateIdentifier.getDefault().checkDate(input.toString());
                return date.getTime();
            } catch (Exception ex) {
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
