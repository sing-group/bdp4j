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
public interface DataWriter {

    /**
     * Save data in a filepath
     *
     * @param filename File name/path where the data is saved
     * @param data Data to save
     */
    void saveData(String filename, Object data);
}
