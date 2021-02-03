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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.bdp4j.transformers.attribute.Enum2IntTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class Enum2IntTransformerTest {

    private final Supplier<Enum2IntTransformer> transformerBuilder;
    private final int expectedNull;
    private final int expectedDate;
    private final int expectedString;
    private final int expectedNumber;

    private Enum2IntTransformer transformer;

    public Enum2IntTransformerTest(
            String testName,
            Supplier<Enum2IntTransformer> transformerBuilder,
            int expectedNull, int expectedDate, int expectedString, int expectedNumber
    ) {
        this.transformerBuilder = transformerBuilder;
        this.expectedNull = expectedNull;
        this.expectedDate = expectedDate;
        this.expectedString = expectedString;
        this.expectedNumber = expectedNumber;
    }

    @Before
    public void setUp() {
        this.transformer = this.transformerBuilder.get();
    }

    @After
    public void tearDown() {
        this.transformer = null;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        Map<String, Integer> transformList = new HashMap<>();
        transformList.put("ham", 0);
        transformList.put("spam", 1);

        Supplier<Enum2IntTransformer> defaultTransformer = () -> new Enum2IntTransformer();

        Supplier<Enum2IntTransformer> parameterizedTransformer = () -> new Enum2IntTransformer(transformList);

        return Arrays.asList(
                //null, date, string, number
                new Object[]{"Default", defaultTransformer, -1, 0, 0, 0},
                new Object[]{"Parameterized", parameterizedTransformer, -1, 3, 1, 2}
        );
    }

    @Test
    public void testTransformNull() {
        double actual = this.transformer.transform(null);

        assertEquals(expectedNull, actual, 0d);
    }

    @Test
    public void testTransformDate() {
        Date input = new Date();
        double actual = this.transformer.transform(input);

        assertEquals(expectedDate, actual, 0d);
    }

    @Test
    public void testTransformString() {
        String input = "spam";
        double actual = this.transformer.transform(input);

        assertEquals(expectedString, actual, 0d);
    }

    @Test
    public void testTransformNumber() {
        Number input = 1.5;
        double actual = this.transformer.transform(input);

        assertEquals(expectedNumber, actual, 0d);
    }
}
