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

public class PipeUtils {

    private static final Logger logger = LogManager.getLogger(PipeUtils.class);
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
