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
 * Trasform an input from String to Double, that represents the input lenght.
 *
 * @author María Novo
 */
public class ComputeStringLenghtTransformer extends Transformer {

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
        return new Double(((String) input).length());
    }


    @Override
    public String getTransformerListValues() {
        return transformerListValues;
    }
    
    public Class<?> getInputType() {
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
