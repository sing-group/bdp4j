package org.bdp4j.transformers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 *
 * @author María Novo
 */
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
