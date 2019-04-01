/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.pipe;

/**
 * Classes that need to save data, like a Singleton class, had to implement this
 * interface.
 *
 * @author María Novo
 */
public interface DataReader {

    /**
     * Retrieve data from filepath
     *
     * @param filename File name/path from retrieve data
     */
    void retrieveData(String filename);
}
