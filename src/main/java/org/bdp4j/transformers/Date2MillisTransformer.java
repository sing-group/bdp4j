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
public class Date2MillisTransformer extends Transformer<String> {

    /**
     * Transform an input, that represents a Date to Double
     *
     * * @param input A string to transform in Double
     */
    public double transform(String input) {
        if (input != null && !input.equals("null")) {
            Date date = DateIdentifier.getDefault().checkDate(input);
            return date.getTime();
        } else {
            return 0;
        }
    }
}
