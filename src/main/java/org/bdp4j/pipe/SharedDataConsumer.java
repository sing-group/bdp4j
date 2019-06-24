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


package org.bdp4j.pipe;


/**
 * This interface is used in case of resume execution from a particular pipe to
 * retrieve data.
 *
 * If you need to read aditional data, for example, a Singleton class, you have
 * to implement this interface, to specify how to read this data.
 *
 * @author María Novo
 */
public interface SharedDataConsumer {
    /**
     * Read data from filepath
     *
     * @param dir Directory path to read data
     */
    public void readFromDisk(String dir);
}
