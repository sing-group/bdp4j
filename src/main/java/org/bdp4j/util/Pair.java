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
package org.bdp4j.util;

import java.io.Serializable;

/**
 * A pair of generic objects implemented by using Java Generics
 *
 * @param <T1> Tipo del primer objeto
 * @param <T2> Tipo del segundo objeto
 * @author José Ramón Méndez
 * Implementa un par de objetos
 * @since JDK 1.5
 */
public final class Pair<T1, T2> implements Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = 3349516261232499121L;
    /**
     * Objeto 1
     */
    T1 obj1;
    /**
     * Objeto 2
     */
    T2 obj2;

    /**
     * Constructor a partir del par
     *
     * @param obj1 El primer objeto
     * @param obj2 El segundo objeto
     */
    public Pair(T1 obj1, T2 obj2) {
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    /**
     * @return Returns the obj1.
     */
    public T1 getObj1() {
        return obj1;
    }

    /**
     * @param obj1 The obj1 to set.
     */
    public void setObj1(T1 obj1) {
        this.obj1 = obj1;
    }

    /**
     * @return Returns the obj2.
     */
    public T2 getObj2() {
        return obj2;
    }

    /**
     * @param obj2 The obj2 to set.
     */
    public void setObj2(T2 obj2) {
        this.obj2 = obj2;
    }
}
