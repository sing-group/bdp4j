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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class Date2MillisTransformerTest {
    private Date2MillisTransformer transformer;
    
    @Before
    public void setUp() {
        this.transformer = new Date2MillisTransformer();
    }
    
    @After
    public void tearDown() {
        this.transformer = null;
    }

    @Test
    public void testTransformDateTime() {
        //double expected = 1444686300000d; // 12/10/2015 23:45:00 
        double expected = 1444693500000d;
        LocalDateTime input = Instant.ofEpochMilli((long)expected).atZone(ZoneId.of("UTC")).toLocalDateTime();
        double actual = this.transformer.transform(input);
        assertEquals(expected, actual, 0d);
    }

    
//    @Test
//    public void testTransformDate() {
//        double expected = 1449705600000d; // 10/12/2015 21:45:00
//        Date input = new Date((long) expected);        
//        double actual = this.transformer.transform(input);
//        
//        assertEquals(expected, actual, 0d);
//    }
    /**
     * Test of transform method, of class Date2MillisTransformer.
     */
    @Test
    public void testTransformString() {
        //double expected = 1444686300000d; // 12/10/2015 23:45:00
        double expected = 1444693500000d;
        String input = "12/10/2015 23:45:00";     
        double actual = this.transformer.transform(input);
        System.out.println(""+actual);
        assertEquals(expected, actual, 0d);
    }
    
    /**
     * Test of transform method, of class Date2MillisTransformer.
     */
    @Test
    public void testTransformNull() {
        double actual = this.transformer.transform(null);
        
        assertEquals(0d, actual, 0d);
    }
}
