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
import org.bdp4j.util.Pair;

/**
 * Transform an input value on an input scale in to an output scale
 *
 * @author María Novo
 */
public class InputScale2OutputScaleTransformer extends Transformer {

    private String transformerListValues;

    /**
     * Input value scale
     */
    private Pair<Double, Double> inputScale;

    /**
     * Output value scale
     */
    private Pair<Double, Double> outputScale;

    /**
     * Build a InputScale2OutputScaleTransformer using input and output scale
     *
     * @param inputScale Input value scale
     * @param outputScale Output value scale
     */
    public InputScale2OutputScaleTransformer(Pair<Double, Double> inputScale, Pair<Double, Double> outputScale) {
        this.inputScale = inputScale;
        this.outputScale = outputScale;
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

            Double min_input = inputScale.getObj1();
            Double max_input = inputScale.getObj2();

            Double min_output = outputScale.getObj1();
            Double max_output = outputScale.getObj2();

            Double value = Double.parseDouble(input.toString());

            Double first_step = (value - min_input) / (max_input - min_input);
            Double second_step = (max_output - min_output);
            Double result = (first_step * second_step) + min_output;
            return result;

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
     * Get a List who contains the transformated values
     *
     * @return List who contains the transformated values
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

//    public static void main(String[] args) {
//        Pair<Double, Double> inputScale = new Pair<>(-1d, 1d);
//        Pair<Double, Double> outputScale = new Pair<>(0d, 10d);
//        InputScale2OutputScaleTransformer ieot = new InputScale2OutputScaleTransformer(inputScale, outputScale);
//        Double res = ieot.transform("");
//
//    }
}
