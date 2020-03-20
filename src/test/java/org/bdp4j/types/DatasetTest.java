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
package org.bdp4j.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.bdp4j.matchers.IsEqualToInstance.containsInstancesInOrder;
import org.hamcrest.CoreMatchers;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;
import weka.core.Attribute;
import weka.core.Instance;

public class DatasetTest {

    Dataset dataset;
    Dataset clone;
    String name = "test";
    ArrayList<Attribute> attributes = new ArrayList<>();
    List<String> target_values;

    @Before
    public void setUp() {
        target_values = new ArrayList<>();
        target_values.add("0");
        target_values.add("1");

        // Expected attributes generic
        attributes.add(new Attribute("id", true));
        attributes.add(new Attribute("length"));
        attributes.add(new Attribute("length_after_drop"));
        attributes.add(new Attribute("bn:00071570n"));
        attributes.add(new Attribute("bn:00019048n"));
        attributes.add(new Attribute("target", target_values));
        dataset = new Dataset(name, attributes, 0);

        Instance instance = this.dataset.createDenseInstance();
        instance.setValue(0, "1");
        instance.setValue(1, 18d);
        instance.setValue(2, 12d);
        instance.setValue(3, 1d);
        instance.setValue(4, 1d);
        instance.setValue(5, 1);
    }

    @After
    public void tearDown() {
        this.dataset = null;
        this.clone = null;
    }

    @Test
    public void testFilterColumnNames() {
        clone = new Dataset(name, attributes, 0);
        List<String> actual = clone.filterColumnNames("^length");

        assertThat(actual, CoreMatchers.hasItems("length", "length_after_drop"));
    }

    @Test
    public void testGetAttributes() {
        List<String> actual = this.dataset.getAttributes();

        assertThat(actual, contains("id", "length", "length_after_drop", "bn:00071570n", "bn:00019048n", "target"));
    }

    @Test
    public void testGetInstances() {

        clone = new Dataset(name, attributes, 0);
        Instance expectedInstance = clone.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, 18d);
        expectedInstance.setValue(2, 12d);
        expectedInstance.setValue(3, 1d);
        expectedInstance.setValue(4, 1d);
        expectedInstance.setValue(5, 1);
        List<Instance> expected = new ArrayList<>();
        expected.add(expectedInstance);

        List<Instance> actual = this.dataset.getInstances();

        assertThat(actual, containsInstancesInOrder(expected));
    }

    @Test
    public void testReplaceColumnNames() {
        // Expected dataset
        attributes = new ArrayList<>();
        attributes.add(new Attribute("id", true));
        attributes.add(new Attribute("length"));
        attributes.add(new Attribute("length_after_drop"));
        attributes.add(new Attribute("drug"));
        attributes.add(new Attribute("target", target_values));
        Dataset expectedDataset = new Dataset(name, attributes, 0);
        Instance expectedInstance = expectedDataset.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, 18d);
        expectedInstance.setValue(2, 12d);
        expectedInstance.setValue(3, 2d);
        expectedInstance.setValue(4, 1);
        List<Instance> expected = new ArrayList<>();
        expected.add(expectedInstance);

        // Actual
        Map<String, String> columnsToReplace = new HashMap<>();
        columnsToReplace.put("bn:00071570n", "drug");
        columnsToReplace.put("bn:00019048n", "drug");

        Dataset actual = this.dataset.replaceColumnNames(columnsToReplace, Dataset.COMBINE_SUM);
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();
        assertThat(actualAttributes, contains("id", "length", "length_after_drop", "drug", "target"));

        assertThat(actualInstances, containsInstancesInOrder(expected));

    }

    @Test
    public void testFilterColumns() {
        // Expected dataset
        ArrayList<Attribute> attribute = new ArrayList<>();
        attribute.add(new Attribute("id", true));
        attribute.add(new Attribute("bn:00071570n"));
        attribute.add(new Attribute("bn:00019048n"));
        attribute.add(new Attribute("target", target_values));
        Dataset expectedDataset = new Dataset(name, attribute, 0);

        Instance expectedInstance = expectedDataset.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, 1d);
        expectedInstance.setValue(2, 1d);
        expectedInstance.setValue(3, 1);
        List<Instance> expected = new ArrayList<>();
        expected.add(expectedInstance);

        Dataset actual = this.dataset.filterColumns("id|bn:00071570n|bn:00019048n|target");
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();

        assertThat(actualAttributes, contains("id", "bn:00071570n", "bn:00019048n", "target"));
        assertThat(actualInstances, containsInstancesInOrder(expected));

    }

    @Test
    public void testDeleteAttributeColumns() {
        // Expected dataset
        ArrayList<Attribute> attribute = new ArrayList<>();
        attribute.add(new Attribute("id", true));
        attribute.add(new Attribute("bn:00071570n"));
        attribute.add(new Attribute("bn:00019048n"));
        attribute.add(new Attribute("target", target_values));
        Dataset expectedDataset = new Dataset(name, attribute, 0);

        Instance expectedInstance = expectedDataset.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, 1d);
        expectedInstance.setValue(2, 1d);
        expectedInstance.setValue(3, 1);
        List<Instance> expected = new ArrayList<>();
        expected.add(expectedInstance);

        List<String> listAttributeName = new ArrayList<>();
        listAttributeName.add("length");
        listAttributeName.add("length_after_drop");

        Dataset actual = this.dataset.deleteAttributeColumns(listAttributeName);
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();

        assertThat(actualAttributes, contains("id", "bn:00071570n", "bn:00019048n", "target"));
        assertThat(actualInstances, containsInstancesInOrder(expected));
    }

    @Test
    public void testJoinAttributeColumns() {

        attributes = new ArrayList<>();
        attributes.add(new Attribute("id", true));
        attributes.add(new Attribute("length"));
        attributes.add(new Attribute("length_after_drop"));
        attributes.add(new Attribute("drug"));
        attributes.add(new Attribute("target", target_values));
        Dataset expectedDataset = new Dataset(name, attributes, 0);
        Instance expectedInstance = expectedDataset.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, 18d);
        expectedInstance.setValue(2, 12d);
        expectedInstance.setValue(3, 2d);
        expectedInstance.setValue(4, 1);
        List<Instance> expected = new ArrayList<>();
        expected.add(expectedInstance);

        List<String> listAttributeName = new ArrayList<>();
        listAttributeName.add("bn:00071570n");
        listAttributeName.add("bn:00019048n");

        Dataset actual = this.dataset.joinAttributeColumns(listAttributeName, "drug", Dataset.COMBINE_SUM);
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();

        assertThat(actualAttributes, contains("id", "length", "length_after_drop", "drug", "target"));
        assertThat(actualInstances, containsInstancesInOrder(expected));
    }

    @Test
    public void testEvaluateColumns() {

        Instance instance = dataset.createDenseInstance();
        instance.setValue(0, "2");
        instance.setValue(1, 15);
        instance.setValue(2, 10d);
        instance.setValue(3, 2d);
        instance.setValue(4, 1d);
        instance.setValue(5, 0);

        instance = dataset.createDenseInstance();
        instance.setValue(0, "3");
        instance.setValue(1, 11);
        instance.setValue(2, 10d);
        instance.setValue(3, 2d);
        instance.setValue(4, 1d);
        instance.setValue(5, 0);

        instance = dataset.createDenseInstance();
        instance.setValue(0, "4");
        instance.setValue(1, 20);
        instance.setValue(2, 10d);
        instance.setValue(3, 2d);
        instance.setValue(4, 1d);
        instance.setValue(5, 0);

        Map<String, Integer> expected = new HashMap<>();
        expected.put("0", 1);
        expected.put("1", 1);

        Map<String, Integer> result = dataset.evaluateColumns("((length > 14.0) && (length < 20.0)) ? 1: 0", int.class, new String[]{"length"}, new Class[]{double.class}, "target");
        assertEquals(expected, result);

        expected = new HashMap<>();
        expected.put("0", 2);
        expected.put("1", 1);

        result = dataset.evaluateColumns("(length > 14.0) ? 1: 0", int.class, new String[]{"length"}, new Class[]{double.class}, "target");
        assertEquals(expected, result);
        
        expected = new HashMap<>();
        expected.put("0", 0);
        expected.put("1", 0);
        
        result = dataset.evaluateColumns("((bn:00071570n > 1) && (bn:00019048n > 1)) ? 1 : 0", int.class, new String[]{"bn:00071570n", "bn:00019048n"}, new Class[]{double.class, double.class}, "target");
        assertEquals(expected, result);

    }

}
