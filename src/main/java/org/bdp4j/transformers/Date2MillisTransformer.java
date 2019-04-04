/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.bdp4j.types.Transformer;
import org.bdp4j.util.DateIdentifier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bdp4j.util.DateTimeIdentifier;

/**
 * Trasform an input from String, that represents a Data to Double
 *
 * @author María Novo
 */
public class Date2MillisTransformer extends Transformer {

    private String transformerListValues;

    /**
     * Transform an input, that represents a Date to Double
     *
     * @param input A string to transform in Double
     * @return A double value that represents a Date
     */
    public double transform(Object input) {
           
        if (input != null && !input.equals("null")) {
            try {
                LocalDateTime date = DateTimeIdentifier.getDefault().checkDateTime(input.toString());
                return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
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

    public Class<?> getInputType() {
        return Date.class;
    }

    /**
     * Get a List who contains the values
     *
     * @return List who contains the values
     */
    @Override
    public List<Integer> getListValues() {
        return new ArrayList<Integer>();
    }
}
