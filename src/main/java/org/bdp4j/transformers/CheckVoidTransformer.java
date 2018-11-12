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

    /**
     * Trasform an input from String to Double, that represents if this input is
     * empty or not.
     *
     * @param input A string to transform in Double
     */
    public double transform(String input) {
        return (input.isEmpty() ? 0 : 1);
    }
}
