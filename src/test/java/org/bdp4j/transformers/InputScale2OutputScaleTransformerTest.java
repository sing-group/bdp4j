/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2020 SING Group (University of Vigo)
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
package org.bdp4j.transformers;

import java.util.ArrayList;
import java.util.List;
import org.bdp4j.transformers.attribute.InputScale2OutputScaleTransformer;
import org.bdp4j.util.Pair;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class InputScale2OutputScaleTransformerTest {

    InputScale2OutputScaleTransformer inputScale2OutputScaleTransformer;

    public InputScale2OutputScaleTransformerTest() {

    }

    @Before
    public void setUp() {
        Pair<Double, Double> inputScale = new Pair<>(-1d, 1d);
        Pair<Double, Double> outputScale = new Pair<>(0d, 10d);
        inputScale2OutputScaleTransformer = new InputScale2OutputScaleTransformer(inputScale, outputScale);
    }

    /**
     * Test of transform method, of class InputScale2OutputScaleTransformer.
     */
    @Test
    public void testTransform() {
        Object input = null;
        double expResult = 0.0;
        double result = inputScale2OutputScaleTransformer.transform(input);
        assertEquals(expResult, result, 0.0);

        input = -0.5;
        expResult = 2.5;
        result = inputScale2OutputScaleTransformer.transform(input);
        assertEquals(expResult, result, 0.0);
        
        input = 0.5;
        expResult = 7.5;
        result = inputScale2OutputScaleTransformer.transform(input);
        assertEquals(expResult, result, 0.0);
    }
   

    /**
     * Test of getInputType method, of class InputScale2OutputScaleTransformer.
     */
    @Test
    public void testGetInputType() {
        Class expResult = String.class;
        Class result = inputScale2OutputScaleTransformer.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getListValues method, of class InputScale2OutputScaleTransformer.
     */
    @Test
    public void testGetListValues() {
        List<Integer> expResult = new ArrayList<>();
        List<Integer> result = inputScale2OutputScaleTransformer.getListValues();
        assertEquals(expResult, result);
    }

}
