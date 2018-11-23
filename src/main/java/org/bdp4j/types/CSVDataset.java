/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor
 *
 * This file is a modification of DefaultDataset class,that is part of  Java 
 * Machine Learning Library, created by Thomas Abeel.
 * Copyright (c) 2006-2010, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 * 
 */
package org.bdp4j.types;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.unmodifiableList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.stream.Collectors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.PipeParameter;

/**
 *  Build a CSVDataset with an attribute list, an instance id list and the Instance list.
 * @author María Novo
 */
public class CSVDataset extends ArrayList<Instance> implements Dataset {
    /**
     * The serial version UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(CSVDataset.class);
    /**
     * The default value for the output file
     */
    public static final String DEFAULT_OUTPUT_FILE = "CSVDataset.csv";
    private TreeSet<Object> classes = new TreeSet<>();
    private List<String> attributes;
    private List<String> instanceIds;
    private String outputFile = DEFAULT_OUTPUT_FILE;

    /**
     * Build a CSVDataset with the specified attribute list values
     */
    public CSVDataset(List<String> attribute) {
        this.attributes = new ArrayList<>(attribute);
    }

    /**
     * Build a CSVDataset with the specified attribute list values using the
     * specified output directory
     */
    public CSVDataset(List<String> attribute, String outputFile) {
        this(attribute);
        this.outputFile = outputFile;
    }

    /**
     * Build a CSVDataset with the specified attribute list values and the
     * specified instance id list
     */
    public CSVDataset(List<String> attribute, List<String> instanceIds) {
        this.attributes = new ArrayList<>(attribute);
        this.instanceIds = instanceIds;
    }
    
    /**
     * Build a CSVDataset with the specified attribute list values and the
     * specified instance id list, using the specified output directory
     */
    public CSVDataset(List<String> attribute, List<String> instanceIds, String outputFile) {
        this(attribute, instanceIds);
        this.outputFile = outputFile;
    }

    /**
     * Set the output filename to store the CSV contents
     *
     * @param output The filename/filepath to store the CSV contents
     */
    @PipeParameter(name = "outputFile", description = "Indicates the output filename/path for saving CSV", defaultValue = DEFAULT_OUTPUT_FILE)
    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    /**
     * Returns the filename where the CSV contents will be stored
     *
     * @return the filename/filepath where the CSV contents will be stored
     */
    public String getOutputFile() {
        return this.outputFile;
    }

    /**
     * Returns the attribute list of CSVDataset
     *
     * @return the attribute list of CSVDataset
     */
    public List<String> getAttributes() {
        return unmodifiableList(this.attributes);
    }

     /**
     * Returns the instance id list of CSVDataset
     *
     * @return the instance id list of CSVDataset
     */
    public List<String> getInstanceIds() {
        return this.instanceIds;
    }

     /**
     * Set the instance id list
     *
     * @param instanceIds The instance id list
     */
    @PipeParameter(name = "instanceIds", description = "Indicates the instance id list of CSVDataset", defaultValue = "")
  
    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

    /**
     * Generates a CSV with dataset content.
     */
    public void generateCSV() {
        try (Writer output = new OutputStreamWriter(new FileOutputStream(this.outputFile))) {
            if (attributes.size() > 0 && instanceIds.size() > 0) {
                String attributesList = this.attributes.stream().map(String::toString).collect(Collectors.joining(";"));
                if (instanceIds.size() == this.size()) {
                    Instance dsInstance;
                    StringBuilder dsRow;
                    StringBuilder dsAttributes = new StringBuilder("id;");
                    dsAttributes.append(attributesList).append("\r\n");
                    output.write(dsAttributes.toString());
                    for (int i = 0; i < this.size(); i++) {
                        dsRow = new StringBuilder();
                        dsInstance = this.get(i);
                        dsRow.append(instanceIds.get(i)).append(";");
                        for (Map.Entry<Integer, Double> entry : dsInstance.entrySet()) {
                            Double value = entry.getValue();
                            dsRow.append(value).append(";");
                        }
                        output.write(dsRow.append("\r\n").toString());
                    }
                    output.flush();
                } else {
                    logger.fatal("Instance list size is different to dataset size. Unable to generate CSV for this dataset.");
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    private void check(Collection<? extends Instance> c) {
        for (Instance i : c) {
            check(i);
        }
    }

    private void check(Instance i) {
        if (i.noAttributes() != this.noAttributes()) {
            throw new IllegalArgumentException("Instance have a different number of features than the dataset");
        }
        if (i.classValue() != null) {
            this.classes().add(i.classValue());
        }
    }

    @Override
    public int noAttributes() {
        return this.attributes.size();
    }

    @Override
    public synchronized boolean addAll(Collection<? extends Instance> c) {
        check(c);
        return super.addAll(c);
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends Instance> c) {
        check(c);
        return super.addAll(index, c);
    }

    @Override
    public synchronized boolean add(Instance e) {
        check(e);
        return super.add(e);
    }

    @Override
    public void add(int index, Instance e) {
        check(e);
        super.add(index, e);
    }

    public synchronized void addElement(Instance e) {
        check(e);
        super.add(e);
    }

    public synchronized void insertElementAt(Instance e, int index) {
        check(e);
        super.add(index, e);
    }

    public synchronized void setElementAt(Instance e, int index) {
        check(e);
        super.set(index, e);
    }

    @Override
    public Instance instance(int index) {
        return super.get(index);
    }

    @Override
    public SortedSet<Object> classes() {
        return classes;
    }

    /**
     * Returns the k instances of the given data set that are the closest to the
     * instance that is given as a parameter.
     *
     * @param dm the distance measure used to calculate the distance between
     * instances
     * @param inst the instance for which we need to find the closest
     * @return the instances from the supplied data set that are closest to the
     * supplied instance
     *
     */
    @Override
    public Set<Instance> kNearest(int k, Instance inst, DistanceMeasure dm) {
        Map<Instance, Double> closest = new HashMap<Instance, Double>();
        double max = dm.getMaxValue();
        for (Instance tmp : this) {
            double d = dm.measure(inst, tmp);
            if (dm.compare(d, max) && !inst.equals(tmp)) {
                closest.put(tmp, d);
                if (closest.size() > k) {
                    max = removeFarthest(closest, dm);
                }
            }

        }
        return closest.keySet();
    }

    /*
     * Removes the element from the vector that is farthest from the supplied
     * element.
     */
    private double removeFarthest(Map<Instance, Double> vector, DistanceMeasure dm) {
        Instance tmp = null;
        double max = dm.getMinValue();
        for (Instance inst : vector.keySet()) {
            double d = vector.get(inst);

            if (dm.compare(max, d)) {
                max = d;
                tmp = inst;
            }
        }
        vector.remove(tmp);
        return max;

    }

    @Override
    public Dataset[] folds(int numFolds, Random rg) {
        Dataset[] out = new Dataset[numFolds];
        List<Integer> indices = new Vector<Integer>();
        for (int i = 0; i < this.size(); i++) {
            indices.add(i);
        }
        int size = (this.size() / numFolds) + 1;
        int[][] array = new int[numFolds][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < numFolds; j++) {
                if (indices.size() > 0) {
                    array[j][i] = indices.remove(rg.nextInt(indices.size()));
                } else {
                    array[j][i] = -1;
                }
            }
        }
        for (int i = 0; i < numFolds; i++) {
            int[] indi;
            if (array[i][size - 1] == -1) {
                indi = new int[size - 1];
                System.arraycopy(array[i], 0, indi, 0, size - 1);
            } else {
                indi = new int[size];
                System.arraycopy(array[i], 0, indi, 0, size);
            }
            out[i] = new Fold(this, indi);

        }
        return out;
    }

    @Override
    public int classIndex(Object clazz) {

        if (clazz != null) {
            return this.classes().headSet(clazz).size();
        } else {
            return -1;
        }

    }

    @Override
    public Object classValue(int index) {
        int i = 0;
        for (Object o : this.classes) {
            if (i == index) {
                return o;
            }
            i++;
        }
        return null;
    }

    @Override
    public Dataset copy() {
        DefaultDataset out = new DefaultDataset();
        for (Instance i : this) {
            out.add(i.copy());
        }
        return out;
    }
}
