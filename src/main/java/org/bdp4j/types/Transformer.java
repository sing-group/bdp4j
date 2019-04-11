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

package org.bdp4j.types;

import java.util.List;

/**
 * To transform columns of a dataset into double representation suitable for using
 * with classifiers.
 *
 * @author María Novo
 */
public abstract class Transformer {

    /**
     * Transform the data
     *
     * @param input Data to transform
     * @return The double representation of the input
     */
    public abstract double transform(Object input);

    public abstract Class<?>  getInputType();
     
    /**
     * Get a String who contains the meaning of the transformated values
     *
     * @return String who contains the meaning of the transformated values
     */
    public abstract String getTransformerListValues();

    /**
     * Get a List who contains the transformated values
     *
     * @return List who contains the transformated values
     */
    public abstract List<Integer> getListValues();
}
