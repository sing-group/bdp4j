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
 * Extends the default boolean behaviour by adding the posibility of using some
 * additional string representation of a boolean for instance
 * <ul>
 * <li> 0 for false and any other number greater than 0 for true </li>
 * <li> "yes" for true and "no" for false </li>
 * <li> "true" for true and "false" for false </li>
 * <li> "on" for true and "off" for false </li>
 * </ul>
 *
 * @author José Ramón Méndez
 */
public class EBoolean {
    /**
     * Decodifies a boolean from a string representation
     *
     * @param value The string representation of the boolean
     * @return the boolean representation of the value
     */
    public static boolean getBoolean(String value) {
        boolean returnValue = false;
        if ("yes".equalsIgnoreCase(value)
                || "true".equalsIgnoreCase(value)
                || "on".equalsIgnoreCase(value)) {
            returnValue = true;
        } else {
            try {
                returnValue = Integer.parseInt(value) > 0;
            } catch (Exception e) {
            }
        }

        return returnValue;
    }
    
    /**
     * Decodifies a boolean from its string representation
     *
     * @param value The string representation of the boolean
     * @return the boolean representation of the value
     */
    public static boolean parseBoolean(String value) {
        return getBoolean(value);
    }

    /**
     * Creates a Boolean object from its string representation
     *
     * @param value The string representation of the boolean
     * @return The Boolean representation of the value
     */
    public static Boolean valueOf(String value) {
        return new Boolean(getBoolean(value));
    }
}
