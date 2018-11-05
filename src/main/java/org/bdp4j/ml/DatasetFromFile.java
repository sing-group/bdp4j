/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.ml;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;



/**
 *
 * @author María Novo
 */
public class DatasetFromFile {
    public void loadFile(String file){
        try {
//            File fileToLoad = new File(Main.class.getResource("/"+file).getPath());
//            Dataset data =  FileHandler.loadDataset(fileToLoad, ";");   
//            System.out.println("org.ski4spam.pipe.impl.DatasetFromFile.loadFile() " + data);
//           int i;
//            for (i=0;i<data.size();i++){
//                System.out.println(data.get(i));
//           }
           
            //System.out.println("org.ski4spam.pipe.impl.DatasetFromFile.loadFile() --> " + data);
        } catch (Exception ex) {
            Logger.getLogger(DatasetFromFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
