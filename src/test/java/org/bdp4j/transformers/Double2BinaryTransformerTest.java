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

import java.util.Date;
import org.bdp4j.transformers.attribute.Double2BinaryTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Double2BinaryTransformerTest {
    Double2BinaryTransformer transformer;
    
    @Before
    public void setUp() {
        this.transformer = new Double2BinaryTransformer();
    }
    
    @After
    public void tearDown() {
        this.transformer=null;
    }
     @Test
    public void testTransformNull() {
        double expected = 0;
        double actual = this.transformer.transform(null);
        
        assertEquals(expected, actual, 0d);
    }

    @Test
    public void testTransformDate() {
        double expected = 0;
        Date input = new Date();
        double actual = this.transformer.transform(input);

        assertEquals(expected, actual, 0d);
    }
    
    @Test
    public void testTransformString() {
        double expected = 1;
        String input = "2";
        double actual = this.transformer.transform(input);

        assertEquals(expected, actual, 0d);
    }
     @Test
    public void testTransformNumber() {
        double expected = 1;
        Number input = 1.5;
        double actual = this.transformer.transform(input);
        
        assertEquals(expected, actual, 0d);
    }

}
