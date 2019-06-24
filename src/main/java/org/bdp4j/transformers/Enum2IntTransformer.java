/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2019 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */



package org.bdp4j.transformers;

import org.bdp4j.types.Transformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Enum2IntTransformer extends Transformer {

    private Map<String, Integer> transformList;

    public Enum2IntTransformer() {
        this(new HashMap<>());
    }

    /**
     * Build a Enum2IntTransformer using a transformList, to assign values to
     * each string
     *
     * @param transformList to assign values to each string
     */
    public Enum2IntTransformer(Map<String, Integer> transformList) {
        this.transformList = transformList;
    }

    /**
     * Build a Enum2IntTransformer using a Java enum, to assign values to each
     * string
     *
     * @param enumType to assign values to each string
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
        if (input != null) {
            if (this.transformList.containsKey(input.toString())) {
                return this.transformList.get(input.toString());
            } else {
                int index = this.getNextIndex();
                this.transformList.put(input.toString(), index);
                return index;
            }
        } 
        return -1;
    }

    /**
     * Get a List who contains the values
     *
     * @return List who contains the values
     */
    @Override
    public List<Integer> getListValues() {
        List<Integer> values = new ArrayList<Integer>();
        for (Map.Entry<String, Integer> entry : transformList.entrySet()) {
            values.add(entry.getValue());
        }
        return values;
    }

    /**
     * Get a String who contains the meaning of the transformated values
     *
     * @return String who contains the meaning of the transformated values
     */
    @Override
    public String getTransformerListValues() {
        StringBuilder values = new StringBuilder();
        for (Map.Entry<String, Integer> entry : transformList.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            values.append(key).append(": ").append(value).append(", ");
        }
        if (values.length() > 0) {
            values.delete(values.length() - 2, values.length() - 1);
        }
        return values.toString();
    }

    public Class<?> getInputType() {
        return String.class;
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
