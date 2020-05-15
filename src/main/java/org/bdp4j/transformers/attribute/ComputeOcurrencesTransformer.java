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
 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers.attribute;

import java.util.ArrayList;
import java.util.List;
import org.bdp4j.types.Transformer;

/**
 * Compute the number of ocurrences of an expression
 *
 * @author María Novo
 */
public class ComputeOcurrencesTransformer extends Transformer {

    private String transformerListValues;

    /**
     * The default value of the regex to split the text
     */
    public static final String DEFAULT_REGEX_VALUE = "([\\W\\s]+)";

    /**
     * Regex value used to compute ocurrences number
     */
    private String regex = DEFAULT_REGEX_VALUE;

    /**
     * Build a ComputeOcurrencesTransformer using regex value
     *
     * @param regex Regex value
     */
    public ComputeOcurrencesTransformer(String regex) {
        this.regex = regex;
    }

    /**
     * Return the regex used to transform input data
     *
     * @return the regex used to transform input data
     */
    public String getRegex() {
        return this.regex;
    }

    /**
     * Estabilish the regex used to transform input data
     *
     * @param regex The regex used to transform input data
     */
    public void setRegex(String regex) {
        this.regex = regex;

    }

    /**
     * Transform an input value from input scale to output scale
     *
     * @param input A value to change from input scale to output scale
     * @return A double value that represents the input value in an output scale
     */
    @Override
    public double transform(Object input) {
        try {
            if (input == null || input.equals("null") || input.equals(" ") || input.equals("")) {
                return 0;
            }
            String[] ocurrences = input.toString().split(this.regex);
            return ocurrences.length;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Get a String who contains the meaning of the transformated values
     *
     * @return String who contains the meaning of the transformated values
     */
    @Override
    public String getTransformerListValues() {
        return transformerListValues;
    }

    /**
     * Return the input type to transform
     *
     * @return the input type to transform
     */
    @Override
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
        return new ArrayList<>();
    }

}
