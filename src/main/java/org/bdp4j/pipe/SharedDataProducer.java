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
package org.bdp4j.pipe;

/**
 * This interface is used in case of resume execution from a particular pipe to
 * save data.
 *
 * If you need to save aditional data, for example, a Singleton class, you have
 * to implement this interface, to specify how to save data.
 *
 * @author María Novo
 */
public interface SharedDataProducer {

    /**
     * Save data in a filepath
     *
     * @param dir Directory path where the data is saved
     */
    void writeToDisk(String dir);
}
