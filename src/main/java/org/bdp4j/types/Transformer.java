/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

/**
 * @param <Object> the input type of transformer
 * @author María Novo
 */
public abstract class Transformer<Object> {

    /**
     * Transform the data
     * @param input Data to transform
     * @return
     */
    public abstract double transform(Object input);
    
    /**
     * Get a String who contents the meaning of the transformated values
     * 
     * @return String who contents the meaning of the transformated values
     */
    public abstract String getTransformerListValues();
}
