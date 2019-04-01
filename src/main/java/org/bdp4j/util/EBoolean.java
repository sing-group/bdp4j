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
