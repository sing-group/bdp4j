/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.types;

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Collections.unmodifiableList;
import java.util.List;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;

/**
 *
 * @author María Novo
 */
public class CSVDataset extends DefaultDataset {

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

    @Override
    public synchronized void addElement(Instance e) {
        check(e);
        super.addElement(e);
    }

    @Override
    public synchronized void insertElementAt(Instance e, int index) {
        check(e);
        super.insertElementAt(e, index);
    }

    @Override
    public synchronized void setElementAt(Instance e, int index) {
        check(e);
        super.setElementAt(e, index);
    }

}
