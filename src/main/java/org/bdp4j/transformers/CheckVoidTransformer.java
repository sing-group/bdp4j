/*
 * BDP4j implements a pipeline framework to allow definining 
 * project pipelines from XML. The main goal of the pipelines of this 
 * application is to transform imput data received from multiple sources 
 * into fully qualified datasets to be used with Machine Learning.
 *
 * Copyright (C) 2018  Sing Group (University of Vigo)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.bdp4j.transformers;

import org.bdp4j.types.Transformer;

import java.util.ArrayList;
import java.util.List;

/**
 * Trasform an input from String to Double, that represents if this input is
 * empty or not.
 *
 * @author María Novo
 */
public class CheckVoidTransformer extends Transformer {

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
    
    public  Class<?>  getInputType(){
     return String.class;   
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
