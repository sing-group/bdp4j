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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.bdp4j.pipe.AbstractPipe;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Instance implements Serializable {

    private static final long serialVersionUID = -8139659995227189017L;

    /**
     * A linked hashmap with the properties
     */
    private Map<String, Serializable> properties = new LinkedHashMap<>();

    /**
     * The input/output data for pipes
     */
    private Serializable data;

    /**
     * The target (label) of the instance
     */
    private Serializable target;

    /**
     * A readable name of the source (for instance the original file or an id)
     * this useful for ML analysis
     */
    private Serializable name;

    /**
     * The instance in its oririnal form (for instance the original file where
     * is stored)
     */
    private Serializable source;

    /**
     * Represents whether the instance is valid or not
     */
    private boolean isValid = true;

    /**
     * Build an Instance from the original attributes keeping properties of the
     * instance void
     *
     * @param data The data to be included in the instance
     * @param target The target (label) of the instance
     * @param name The name (id) of the instance
     * @param source The original form of the instance (often this is the same
     * as data)
     */
    public Instance(Serializable data, Serializable target, Serializable name, Serializable source) {
        this.data = data;
        this.target = target;
        this.name = name;
        this.source = source;
    }

    /**
     * Creates an instance using the information of another one (the new
     * instance is a clon from the older one)
     *
     * @param i The instance to be used as source for creating the new one
     */
    public Instance(Instance i) {
        this.data = (Serializable) cloneObject(i.data);
        this.target = i.target;
        this.name = i.name;
        this.source = i.source;
        this.properties = i.properties;
    }

    /**
     * This is a copy method based on instrospection API. A sealization-based
     * method is still posible Lets see if this solution is good enought
     *
     * @param obj Object to clone
     * @return A new copy of the source object
     */
    public Serializable cloneObject(Serializable obj) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Serializable) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Clone the instance into a new type
     *
     * @return a new instance cloning the original one
     */
    public Instance clone() {
        Instance returnValue = new Instance((Serializable) cloneObject(data), target, name, source);
        returnValue.properties = properties;
        return returnValue;
    }

    /**
     * Returns the data object included in the Instance
     *
     * @return The data object included in the Instance
     */
    public Serializable getData() {
        return data == null ? "NULL" : data;
    }

    /**
     * Stablish the data for the current instance
     *
     * @param d Data to be included in the instance
     */
    public void setData(Serializable d) {
        data = d;
    }

    /**
     * Retrieve the target classification (label) of the instance
     *
     * @return the target classification (label) of the instance
     */
    public Serializable getTarget() {
        return target == null ? "NULL" : target;
    }

    /**
     * Changes the target classification (label) of the instance
     *
     * @param t target classification of the instance
     */
    public void setTarget(Serializable t) {
        target = t;
    }

    /**
     * Retrieve the name of the current instance (id)
     *
     * @return name (id) of the current instance
     */
    public Object getName() {
        return name == null ? "NULL" : name;
    }

    /**
     * Changes the name of the current instance (id)
     *
     * @param n New name (id) for the current instance
     */
    public void setName(Serializable n) {
        name = n;
    }

    /**
     * Rerieve the source of the current instance (usually the file where is
     * stored)
     *
     * @return source of the current instance (usually a file)
     */
    public Object getSource() {
        return source == null ? "NULL" : source;
    }

    /**
     * Changes the source of the current instance (the file where the original
     * instance data is stored)
     *
     * @param s The new source especification for the data (usually a file)
     */
    public void setSource(Serializable s) {
        source = s;
    }

    /**
     * Compiles a set of sorted properties for the current instance
     *
     * @return sorted set contaninig all stored properties
     */
    public synchronized Set<String> getPropertyList() {
        return properties.keySet();
    }

    /**
     * Compile a sorted set of values for the current instance
     *
     * @return set of values of all properties stored
     */
    public synchronized Collection<Serializable> getValueList() {
        return properties.values();
    }

    /**
     * Changes (or add) a specific property for the instance
     *
     * @param key The key to be stored
     * @param value The value for the key
     */
    public synchronized void setProperty(String key, Serializable value) {
        properties.put(key, value);
    }

    /**
     * Retrieves a property of an instance
     *
     * @param key The key for the property
     * @return the value for the specific key
     */
    public synchronized Object getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Indicates if a speficic property is present in the Instance
     *
     * @param key The specific key
     * @return true if the key exists, false otherwise
     */
    public synchronized boolean hasProperty(String key) {
        return (properties != null && properties.containsKey(key));
    }

    /**
     * Returns the string representation of a instance
     *
     * @return A String representation of an instance
     */
    public String toString() {
        if (name instanceof File) {
            return ((File) name).getAbsolutePath();
        } else if (name instanceof String) {
            return (String) name;
        } else {
            return name.toString();
        }
    }

    /**
     * Marks instance as invalid
     */
    public void invalidate() {
        this.isValid = false;
    }

    /**
     * Determine whether the instance is valid or not
     *
     * @return A boolean indicating if the instance is valid or not
     */
    public boolean isValid() {
        return isValid;
    }

}
