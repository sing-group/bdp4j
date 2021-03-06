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

/**
 * An mutable object representation of a boolean
 * @author José Ramón Méndez
 */
public class BooleanBean {
    /**
     * The value that is stored
     */
    private boolean value;

    /**
     * Programming 
     * @param value The value to be stored
     */
    private BooleanBean() {
        
    }

    /**
     * The constructor with a value
     * @param value The value to be stored
     */
    public BooleanBean(boolean value) {
        this.value = value;
    }

    /**
     * Returns the stored value
     * @return The stored value
     */
    public boolean getValue() {
        return value;
    }

    /**
     * Changes the inner boolean value
     * @param value the value to be stored
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    /**
     * Makes an OR between the parameter and the stored value
     * @param value The value to make an OR
     */
    public void Or(boolean value) {
        this.value = this.value || value;
    }

    /**
     * Makes an AND between the parameter and the stored value
     * @param value the value to make an AND
     */
    public void And(boolean value) {
        this.value = this.value && value;
    }
}
