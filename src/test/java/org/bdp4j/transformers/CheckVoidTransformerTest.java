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

package org.bdp4j.transformers;

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
