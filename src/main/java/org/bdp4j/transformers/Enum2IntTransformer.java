/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers;

import java.util.HashMap;
import java.util.Map;
import org.bdp4j.types.Transformer;

/**
 * Transform an input to Double, using transformList values
 * @author María Novo
 */
public class Enum2IntTransformer extends Transformer {

    /**
     * Represents the conversor from String to Integer
     */
    private Map<String, Integer> transformList;
    

    /**
     * Build a Enum2IntTransformer using the default information
     */
    public Enum2IntTransformer() {
        this(new HashMap<>());
    }

    /**
     * Build a Enum2IntTransformer using a transformList, to assign values to
     * each string
     * @param transformList to assign values to each string
     */
    public Enum2IntTransformer(Map<String, Integer> transformList) {
        this.transformList = transformList;
    }

    /**
     * Build a Enum2IntTransformer using a Java enum, to assign values to each
     * string
     * @param enumType to assign values to each string
     * 
     */
    public Enum2IntTransformer(Class<? extends Enum<?>> enumType) {
        this();

        Enum<?>[] enumConstants = enumType.getEnumConstants();
        for (int i = 0; i < enumConstants.length; i++) {
            this.transformList.put(enumConstants[i].name(), i);
        }
    }
    
    /**
     * Transform an input to Double, using transformList values
     *
     * @param input A string to transform in Double
     */
    @Override
    public double transform(Object input) {
        if (this.transformList.containsKey(input.toString())) {
            return this.transformList.get(input.toString());
        } else {
            int index = this.getNextIndex();
            this.transformList.put(input.toString(), index);
            return index;
        }
    }
    
    /**
     * Get a String who contents the meaning of the transformated values
     * 
     * @return String who contents the meaning of the transformated values
     */
    @Override
    public String getTransformerListValues() {
        StringBuilder values = new StringBuilder();
        for(Map.Entry<String, Integer> entry : transformList.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            values.append(key).append(": ").append(value).append(", ");
        }
        if (values.length()>0){
            values.delete(values.length()-2, values.length()-1);
        }
        return values.toString();
    }

    /**
     * Get the next index from transformList values
     * 
     * @return int Contains the next index from transformList
     */
    private int getNextIndex() {
        int maxIndex = this.transformList.values().stream()
                .mapToInt(Integer::intValue)
                .max().orElse(-1);

        return maxIndex + 1;
    }
    
}
