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

    @Before
    public void setUp() {

        attributes.add(new Attribute("id", true));
        attributes.add(new Attribute("length", true));
        attributes.add(new Attribute("length_after_drop", true));
        attributes.add(new Attribute("viagra", true));
        attributes.add(new Attribute("cialis", true));
        attributes.add(new Attribute("target", true));
        dataset = new Dataset(name, attributes, 0);

        Instance instance = this.dataset.createDenseInstance();
        instance.setValue(0, "1");
        instance.setValue(1, "18");
        instance.setValue(2, "12");
        instance.setValue(3, "1");
        instance.setValue(4, "1");
        instance.setValue(5, "1");
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
        List<Instance> actual = this.dataset.getInstances();
        clone = new Dataset(name, attributes, 0);
        //Expected instance
        Instance expectedInstance = clone.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, "18");
        expectedInstance.setValue(2, "12");
        expectedInstance.setValue(3, "1");
        expectedInstance.setValue(4, "1");
        expectedInstance.setValue(5, "1");

        assertThat(actual, containsInstancesInOrder(expectedInstance));
    }

    @Test
    public void testReplaceColumnNames() {
        clone = new Dataset(name, attributes, 0);

        // Param
        Map<String, String> columnsToReplace = new HashMap<>();
        columnsToReplace.put("viagra", "drug");
        columnsToReplace.put("cialis", "drug");

        //Expected instance
        Instance expectedInstance = clone.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, "18");
        expectedInstance.setValue(2, "12");
        expectedInstance.setValue(3, "2");
        expectedInstance.setValue(4, "1");
        expectedInstance.setValue(5, "");
        List<Instance> instances = new ArrayList<>();
        instances.add(expectedInstance);

        Dataset actual = clone.replaceColumnNames(columnsToReplace, Dataset.COMBINE_SUM);
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();

        assertThat(actualAttributes, contains("id", "length", "length_after_drop", "drug", "target"));
        assertThat(actualInstances, containsInstancesInOrder(instances));

    }

    
    @Test
    public void testFilterColumns() {
        // Expected dataset
        ArrayList<Attribute> expectedAttributes = new ArrayList<>();
        expectedAttributes.add(new Attribute("id", true));
        expectedAttributes.add(new Attribute("viagra", true));
        expectedAttributes.add(new Attribute("cialis", true));
        expectedAttributes.add(new Attribute("target", true));
        Dataset expected = new Dataset(name, expectedAttributes, 0);

        Instance expectedInstance = expected.createDenseInstance();
        expectedInstance.setValue(0, "1");
        expectedInstance.setValue(1, "1");
        expectedInstance.setValue(2, "1");
        expectedInstance.setValue(3, "1");
        List<Instance> instances = new ArrayList<>();
        instances.add(expectedInstance);

        clone = this.dataset;
        Dataset actual = clone.filterColumns("id|viagra|cialis|target");
        List<String> actualAttributes = actual.getAttributes();
        List<Instance> actualInstances = actual.getInstances();

        assertThat(actualAttributes, contains("id",  "viagra", "cialis", "target"));
        assertThat(actualInstances, containsInstancesInOrder(instances));

    }
}
