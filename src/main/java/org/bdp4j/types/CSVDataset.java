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
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

/**
 *
 * @author María Novo
 */
public class CSVDataset extends ArrayList<Instance> implements Dataset {

    private TreeSet<Object> classes = new TreeSet<>();
    private List<String> attributes;
    private List<String> instanceIds;

    public CSVDataset(List<String> attribute) {
        this.attributes = new ArrayList<>(attribute);
    }

    public CSVDataset(List<String> attribute, List<String> instanceIds) {
        this.attributes = new ArrayList<>(attribute);
        this.instanceIds = instanceIds;
    }

    public List<String> getAttributes() {
        return unmodifiableList(this.attributes);
    }

    public List<String> getInstanceIds() {
        return this.instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
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
