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
package org.bdp4j.pipe;

import org.bdp4j.types.Instance;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class CombinePropertiesPipeTest {

    String data = "December is here";
    String name = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";
    String source = "basic_example/_spam_/7c63a8fd7ae52e350e354d63b23e1c3b.tsms";

    private static Instance carrier = null;

    private CombinePropertiesPipe instance;
    private CombinePropertiesPipe badInstance;

    public CombinePropertiesPipeTest() {
    }

    @Before
    public void setUp() {
        instance = new CombinePropertiesPipe("length - length_after_drop", Integer.class,
                new String[] { "length", "length_after_drop" }, new Class[] { Integer.class, Integer.class });
        badInstance = new CombinePropertiesPipe("(length - length_after_drop) / wordcounter", Integer.class,
                new String[] { "length", "length_after_drop", "wordcounter" },
                new Class[] { Integer.class, Integer.class, Integer.class });
        carrier = new Instance(new StringBuffer(data), null, name, source);
        carrier.setProperty("length", 16);
        carrier.setProperty("length_after_drop", 14);
    }

    /**
     * Test of getInputType method, of class CombinePropertiesPipe.
     */
    @Test
    public void testGetInputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getInputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getOutputType method, of class CombinePropertiesPipe.
     */
    @Test
    public void testGetOutputType() {
        Class expResult = StringBuffer.class;
        Class result = instance.getOutputType();
        assertEquals(expResult, result);
    }

    /**
     * Test of setCombineProp method, of class CombinePropertiesPipe.
     */
    @Test
    public void testSetCombineProp() {
        String combineProp = "combine";
        instance.setCombineProp(combineProp);
    }

    /**
     * Test of getCombineProp method, of class CombinePropertiesPipe.
     */
    @Test
    public void testGetCombineProp() {
        String expResult = "combine";
        String result = instance.getCombineProp();
        assertEquals(expResult, result);
    }

    /**
     * Test of pipe method, of class CombinePropertiesPipe.
     */
    @Test
    public void testPipe() {
        // Right parameters test
        Instance expResult = new Instance(new StringBuffer(data), null, name, source);
        expResult.setProperty("length", 16);
        expResult.setProperty("length_after_drop", 14);
        
        Instance result = badInstance.pipe(carrier);
        assertEquals(expResult, result);

        // Wrong parameters test
        expResult = new Instance(new StringBuffer(data), null, name, source);
        expResult.setProperty("length", 16);
        expResult.setProperty("length_after_drop", 14);
        expResult.setProperty("combine", 2);

        result = instance.pipe(carrier);
        assertEquals(expResult, result);
    }

}
