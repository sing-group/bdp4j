/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.transformers;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
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

    /**
     * Test of transform method, of class Date2MillisTransformer.
     */
    @Test
    public void testTransformDate() {
        double expected = 1444686300000d; // 10/12/2015 21:45:00

        Date input = new Date((long) expected);
        
        double actual = this.transformer.transform(input);
        
        assertEquals(expected, actual, 0d);
    }

    /**
     * Test of transform method, of class Date2MillisTransformer.
     */
//    @Test
//    public void testTransformString() {
//        double expected = 1444686300000d; // 10/12/2015 9:45:00
//
//        String input = "10/12/2015 21:45:00";
//        
//        double actual = this.transformer.transform(input);
//        
//        assertEquals(expected, actual, 0d);
//    }

    /**
     * Test of transform method, of class Date2MillisTransformer.
     */
    @Test
    public void testTransformNull() {
        double actual = this.transformer.transform(null);
        
        assertEquals(0d, actual, 0d);
    }
}
