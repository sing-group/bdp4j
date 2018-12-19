/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2010, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 * 
 */
package org.bdp4j.types;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.ArrayList;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;

class Fold implements Dataset {
    private int[] indices;

    private Dataset parent;

    public Fold(Dataset parent, int[] indices) {
        this.indices = indices;
        this.parent = parent;
    }

    @Override
    public boolean add(net.sf.javaml.core.Instance i) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public SortedSet<Object> classes() {
        return parent.classes();
    }

    @Override
    public Dataset[] folds(int numFolds, Random rg) {
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public net.sf.javaml.core.Instance instance(int index) {
        return parent.instance(indices[index]);
    }

    @Override
    public Set<net.sf.javaml.core.Instance> kNearest(int k, net.sf.javaml.core.Instance inst,DistanceMeasure dm) {
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public void add(int index, net.sf.javaml.core.Instance element) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public boolean addAll(Collection<? extends net.sf.javaml.core.Instance> c) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public boolean addAll(int index, Collection<? extends net.sf.javaml.core.Instance> c) {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Cannot do this on a fold of a dataset");

    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public net.sf.javaml.core.Instance get(int index) {
        return instance(index);
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    class FoldIterator implements ListIterator<net.sf.javaml.core.Instance> {

        private int currentIndex = 0;

        public FoldIterator(int index) {
            this.currentIndex = index;
        }

        public FoldIterator() {
            this(0);
        }

        @Override
        public boolean hasNext() {
            return currentIndex < indices.length;
        }

        @Override
        public net.sf.javaml.core.Instance next() {
            currentIndex++;
            return instance(currentIndex - 1);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("You cannot do this on a fold.");

        }

        @Override
        public void add(net.sf.javaml.core.Instance arg0) {
            throw new UnsupportedOperationException("You cannot do this on a fold.");

        }

        @Override
        public boolean hasPrevious() {
            return currentIndex > 0;
        }

        @Override
        public int nextIndex() {
            return currentIndex;
        }

        @Override
        public net.sf.javaml.core.Instance previous() {
            currentIndex--;
            return instance(currentIndex);
        }

        @Override
        public int previousIndex() {
            return currentIndex;
        }

        @Override
        public void set(net.sf.javaml.core.Instance arg0) {
            throw new UnsupportedOperationException("You cannot do this on a fold.");

        }

    }

    @Override
    public Iterator<net.sf.javaml.core.Instance> iterator() {
        return new FoldIterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException("Method is not yet implemented");
    }

    @Override
    public ListIterator<net.sf.javaml.core.Instance> listIterator() {
        return new FoldIterator();
    }

    @Override
    public ListIterator<net.sf.javaml.core.Instance> listIterator(int index) {
        return new FoldIterator(index);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public net.sf.javaml.core.Instance remove(int index) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public net.sf.javaml.core.Instance set(int index, net.sf.javaml.core.Instance element) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public int size() {
        return indices.length;
    }

    @Override
    public List<net.sf.javaml.core.Instance> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException("You cannot do this on a fold.");
    }

    @Override
    public Object[] toArray() {
        Object[] out = new Object[indices.length];
        for (int i = 0; i < size(); i++) {
            out[i] = instance(i);
        }
        return out;

    }

     @Override
    public <T> T[] toArray(T[] a) {
        
        List<Instance> tmp = new ArrayList<>();
        for (net.sf.javaml.core.Instance i : this) {
			tmp.add((Instance) i);
        }
        
        return tmp.toArray(a);
    }

    @Override
    public int noAttributes() {
        return parent.noAttributes();
    }

    @Override
    public int classIndex(Object clazz) {
        return parent.classIndex(clazz);
    }

    @Override
    public Object classValue(int index) {
        return parent.classValue(index);
    }

    @Override
    public Dataset copy() {
        Dataset out=new DefaultDataset();
        for(net.sf.javaml.core.Instance i:this)
            out.add(i.copy());
        return out;
    }
}