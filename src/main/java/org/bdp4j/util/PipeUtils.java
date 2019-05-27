/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.ResumableSerialPipes;

/**
 *
 * @author María Novo
 */
public class PipeUtils {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(PipeUtils.class);
    /**
     * Default configuration
     */
    private static final Configurator configurator = Configurator.getLastUsed();

    /**
     * Path where aditional information is saved
     */
    private static final String sharedDataPath = configurator.getProp(Configurator.TEMP_FOLDER) + System.getProperty("file.separator") + "sharedData";

    /**
     * Get path where aditional information is saved
     *
     * @return Shared data path
     */
    public static String getSharedDataPath() {
        return sharedDataPath;
    }

    /**
     * Generate a md5 from a String
     *
     * @param name String name to generate a md5
     * @return a md5 from String
     */
    public static String generateMD5(String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] base64Name = Base64.getEncoder().encode(name.getBytes());
            md.update(base64Name);

            StringBuilder md5Name = new StringBuilder();
            for (byte b : md.digest()) {
                md5Name.append(String.format("%02x", b & 0xff));
            }
            return md5Name.toString();
        } catch (NoSuchAlgorithmException ex) {
            java.util.logging.Logger.getLogger(ResumableSerialPipes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * Retrieve data from file
     *
     * @param filename File name to retrieve data
     * @return an Object with the deserialized retrieve data
     */
    public static Object readFromDisk(String filename) {
        File file = new File(filename);
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            ObjectInputStream input = new ObjectInputStream(buffer);

            return input.readObject();

        } catch (Exception ex) {
            logger.error("[READ FROM DISK] " + ex.getMessage());
        }
        return null;
    }

    /**
     * Saved data in a file
     *
     * @param filename File name where the data is saved
     * @param carriers Data to save
     */
    public static void writeToDisk(String filename, Object carriers) {
        try (FileOutputStream outputFile = new FileOutputStream(filename);
                BufferedOutputStream buffer = new BufferedOutputStream(outputFile);
                ObjectOutputStream output = new ObjectOutputStream(buffer);) {

            if (carriers instanceof String) {
                output.writeObject(carriers.toString());
            } else {
                output.writeObject(carriers);
            }
            output.flush();
        } catch (Exception ex) {
            logger.error("[WRITE TO DISK] " + ex.getMessage());
        }
    }
}
