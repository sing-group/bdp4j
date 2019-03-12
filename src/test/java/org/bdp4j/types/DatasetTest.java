/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.bdp4j.matchers.IsEqualToInstance.containsInstancesInOrder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.*;
import org.junit.Test;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 *
 * @author María Novo
 */
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
        attributes.add(new Attribute("viagra"));
        attributes.add(new Attribute("cialis"));
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

        assertThat(actual, contains("id", "length", "length_after_drop", "viagra", "cialis", "target"));
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
        columnsToReplace.put("viagra", "drug");
        columnsToReplace.put("cialis", "drug");

        Dataset actual = this.dataset.replaceColumnNames(columnsToReplace, Dataset.COMBINE_SUM);
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();
        System.out.println("actualAttributes " + actualAttributes);
        System.out.println("actual Instances " + actualInstances);
        assertThat(actualAttributes, contains("id", "length", "length_after_drop", "drug", "target"));

        assertThat(actualInstances, containsInstancesInOrder(expected));

    }

    @Test
    public void testFilterColumns() {
        // Expected dataset
        ArrayList<Attribute> attribute = new ArrayList<>();
        attribute.add(new Attribute("id", true));
        attribute.add(new Attribute("viagra"));
        attribute.add(new Attribute("cialis"));
        attribute.add(new Attribute("target", target_values));
        Dataset expectedDataset = new Dataset(name, attribute, 0);

        Instance expectedInstance = expectedDataset.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, 1d);
        expectedInstance.setValue(2, 1d);
        expectedInstance.setValue(3, 1);
        List<Instance> expected = new ArrayList<>();
        expected.add(expectedInstance);

        Dataset actual = this.dataset.filterColumns("id|viagra|cialis|target");
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();

        assertThat(actualAttributes, contains("id", "viagra", "cialis", "target"));
        assertThat(actualInstances, containsInstancesInOrder(expected));

    }

    @Test
    public void testDeleteAttributeColumns() {
        // Expected dataset
        ArrayList<Attribute> attribute = new ArrayList<>();
        attribute.add(new Attribute("id", true));
        attribute.add(new Attribute("viagra"));
        attribute.add(new Attribute("cialis"));
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
        System.out.println("actualInstances: " + actualInstances + " - expected: " + expected);
        assertThat(actualAttributes, contains("id", "viagra", "cialis", "target"));
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
        listAttributeName.add("viagra");
        listAttributeName.add("cialis");

        Dataset actual = this.dataset.joinAttributeColumns(listAttributeName, "drug", Dataset.COMBINE_SUM);
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();

        assertThat(actualAttributes, contains("id", "length", "length_after_drop", "drug", "target"));
        assertThat(actualInstances, containsInstancesInOrder(expected));
    }

}
