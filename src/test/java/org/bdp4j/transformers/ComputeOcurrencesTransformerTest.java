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
import org.bdp4j.transformers.attribute.ComputeOcurrencesTransformer;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class ComputeOcurrencesTransformerTest {

    private ComputeOcurrencesTransformer transformer;

    @Before
    public void setUp() {
        String regex = " -- ";
        transformer = new ComputeOcurrencesTransformer(regex);
    }

    /**
     * Test of getRegex method, of class ComputeOcurrencesTransformer.
     */
    @Test
    public void testGetRegex() {
        String expResult = " -- ";
        String result = transformer.getRegex();
        assertEquals(expResult, result);
    }

    /**
     * Test of setRegex method, of class ComputeOcurrencesTransformer.
     */
    @Test
    public void testSetRegex() {
        String regex = " -- ";
        transformer.setRegex(regex);
    }

    /**
     * Test of transform method, of class ComputeOcurrencesTransformer.
     */
    @Test
    public void testTransform() {
        Object input = "this -- oh my God -- oh my -- oh -- not -- my God -- is it -- here -- God -- ";
        double expResult = 9.0;
        transformer.setRegex(" -- ");
        double result = transformer.transform(input);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getTransformerListValues method, of class
     * ComputeOcurrencesTransformer.
     */
    @Test
    public void testGetTransformerListValues() {
        String expResult = null;
        String result = transformer.getTransformerListValues();
        assertEquals(expResult, result);
    }

    /**
     * Test of getInputType method, of class ComputeOcurrencesTransformer.
     */
    @Test
    public void testGetInputType() {
        Class expResult = String.class;
        Class result = transformer.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getListValues method, of class ComputeOcurrencesTransformer.
     */
    @Test
    public void testGetListValues() {
        List<Integer> expResult = new ArrayList<>();
        List<Integer> result = transformer.getListValues();
        assertEquals(expResult, result);
    }

}
