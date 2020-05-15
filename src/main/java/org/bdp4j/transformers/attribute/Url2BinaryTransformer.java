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
 * Transform an input url to double, that represents if this input has an url or
 * not.
 *
 * @author María Novo
 */
public class Url2BinaryTransformer extends Transformer {

    private String transformerListValues;

    /**
     * Transform an input url to double, that represents if this input has an
     * url or not.
     *
     * @param input A url to transform in 0 or 1
     * @return A double value that represents if contains a url or not
     */
    @Override
    public double transform(Object input) {
        try {
            return (((input.toString().indexOf("http:")) != -1 || (input.toString().indexOf("https:")) != -1 || (input.toString().indexOf("www.")) != -1) ? 1 : 0);
        } catch (NullPointerException ex) {
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
}
