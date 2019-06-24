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


package org.bdp4j.util;

import java.io.Serializable;

public final class Pair<T1, T2> implements Serializable {

    private static final long serialVersionUID = 3349516261232499121L;
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
