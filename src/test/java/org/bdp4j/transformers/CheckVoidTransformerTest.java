package org.bdp4j.transformers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author María Novo
 */
public class CheckVoidTransformerTest {

    private CheckVoidTransformer transformer;

    @Before
    public void setUp() {
        transformer = new CheckVoidTransformer();
    }

    @After
    public void tearDown() {
        this.transformer = null;
    }

    @Test
    public void testTransformNull() {
        double expected = 0;
        double actual = this.transformer.transform(null);
        assertEquals(expected, actual, 0d);
    }

    @Test
    public void testTransformDate() {
        double expected = 1;
        Date input = new Date();

        double actual = this.transformer.transform(input);

        assertEquals(expected, actual, 0d);
    }
    
    @Test
    public void testTransformString() {
        double expected = 1;
        String input = "prueba";

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
