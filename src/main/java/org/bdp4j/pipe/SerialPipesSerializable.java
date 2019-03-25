/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.pipe;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import java.security.*;
import java.util.Base64;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.PipeProvider;

/**
 *
 * @author María Novo
 */
public class SerialPipesSerializable extends SerialPipes {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(SerialPipes.class);

    /* Singleton configuration instance */
    private static Configurator configurator = Configurator.getInstance("./config/configuration.xml");

    private static String SERIALIZABLE_PATH = Configurator.TMP_FOLDER;
    private static String DEBUG_MODE = "yes";
    private static String SERIALIZABLE_MODE = "yes";
    /**
     * AbstractPipe list
     */
    private ArrayList<AbstractPipe> pipes;

    /**
     * Build an empty SerialPipes
     */
    public SerialPipesSerializable() {
        super();
    }

    /**
     * Build an empty SerialPipes
     */
    public SerialPipesSerializable(AbstractPipe[] pipes) {
        super(pipes);
    }

    /**
     * Saved data in a file
     *
     * @param filename File name where the data is saved
     * @param carriers Data to save
     */
    public void writeFile(String filename, Object carriers) {
        try (FileOutputStream outputFile = new FileOutputStream(filename);
                BufferedOutputStream buffer = new BufferedOutputStream(outputFile);
                ObjectOutputStream output = new ObjectOutputStream(buffer);) {
            if (carriers instanceof String) {
                output.writeObject(carriers.toString());
            } else {
                output.writeObject(carriers);
            }
            output.close();
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SerialPipesSerializable.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieve data from file
     *
     * @param filename File name to retrieve data
     * @return an Object with the deserialized retrieve data
     */
    public Object readFile(String filename) {
        File file = new File(filename);
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            ObjectInputStream input = new ObjectInputStream(buffer);

            return input.readObject();

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SerialPipesSerializable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    /**
     * AbstractPipe a collection of instances through the whole process. 
     * In addiction, it calculates the step to continue execution,
     * depending on the configuration.
     *
     * @param carriers The instances to be processed
     * @return the instances after processing them
     */
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        int step = 0;
        if (SERIALIZABLE_MODE.equals("yes")) {
            // Calculate pipe to continue execution
            AbstractPipe[] pipeList = super.getPipes();

            String md5PipeName = generateMD5(this.toString());
            File sourcePath = new File(SERIALIZABLE_PATH + "/" + md5PipeName);
            // Get all files but txt with the serialized instances
            FileFilter filter = (File pathname) -> {
                if (pathname.getPath().endsWith(md5PipeName + ".txt")) {
                    return false;
                }
                return true;
            };
            // Saved file list
            File[] listFiles = sourcePath.listFiles(filter);

            if (sourcePath.exists() && sourcePath.isDirectory() && listFiles.length > 0) {
                // if exists check if file with instances matches with md5PipeName.txt
                Arrays.sort(sourcePath.listFiles(), (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));

                File lastModifiedFile = listFiles[0];
                String filename = lastModifiedFile.getName();
                for (step = 0; step < pipeList.length; step++) {
                    AbstractPipe p = pipeList[step];
                    // Generates filename from current pipe
                    String pipefilename = sourcePath + "/" + step + "_" + p.toString() + ".ser";
                    // Check if are there saved data to this pipe, so check if filenames matches
                    if ((sourcePath + "/" + filename).equals(pipefilename)) {
                        // Generate MD5 to carriers
                        StringBuilder md5Carriers = new StringBuilder();
                        carriers.stream().map((carrier) -> generateMD5(carrier.toString())).forEachOrdered((md5Carrier) -> {
                            md5Carriers.append(md5Carrier);
                        });

                        // Las instancias coinciden, por lo que se trata de la misma ejecución, se puede ejecutar desde el paso X
                        String deserializedCarriers = (String) readFile(sourcePath + "/" + md5PipeName + ".txt");
                        // If instances match, the pipe and instances are the same, so, this is the first step
                        if (deserializedCarriers.equals(md5Carriers.toString())) {
                            String[] pipeIndex = filename.split("_");
                            step = Integer.parseInt(pipeIndex[0]) + 1;
                            Collection<Instance> instances = (Collection<Instance>) readFile(sourcePath + "/" + filename);
                            return this.pipeAll(instances, step);
                        }
                    }
                }
            }
        }
        return this.pipeAll(carriers, step);
    }

    /**
     * AbstractPipe a collection of instances through the whole process, from de defined step and save
     * this, depending of the configuration.
     *
     * @param step The index of instance to start processing
     * @param carriers The instances to be processed
     * @return the instances after processing them.
     */
    public Collection<Instance> pipeAll(Collection<Instance> carriers, int step) {
        try {
            int i = 0;
            AbstractPipe p = null;
            AbstractPipe[] pipeList = super.getPipes();
            if (SERIALIZABLE_MODE.equals("yes")) {
                File sourcePath = new File(SERIALIZABLE_PATH);
                if (!sourcePath.exists()) {
                    sourcePath.mkdir();
                }

                // Generate MD5
                String md5PipeName = generateMD5(this.toString());
                if (!md5PipeName.equals("")) {
                    // Generate MD5 to carriers
                    StringBuilder md5Carriers = new StringBuilder();
                    carriers.stream().map((carrier) -> generateMD5(carrier.toString())).forEachOrdered((md5Carrier) -> {
                        md5Carriers.append(md5Carrier);
                    });

                    File path = new File(SERIALIZABLE_PATH + "/" + md5PipeName + "/");
                    if (!path.exists()) {
                        path.mkdir();
                    }

                    // Create file with md5Carrier
                    if (sourcePath.exists() && sourcePath.isDirectory()) {
                        File instancesFile = new File(path.getPath() + "/" + md5PipeName + ".txt");
                        writeFile(instancesFile.getPath(), md5Carriers.toString());
                    }

                    for (i = step; i < pipeList.length; i++) {
                        p = pipeList[i];
                        if (sourcePath.exists() && sourcePath.isDirectory()) {
                            if (p == null) {
                                logger.fatal("AbstractPipe " + i + " is null");
                                System.exit(-1);
                            } else {
                                p.pipeAll(carriers);
                            }
                        }

                        // Guardar instancias
                        String filename = path + "/" + i + "_" + p.toString() + ".ser";
                        if (DEBUG_MODE.equals("yes")) {
                            writeFile(filename, carriers);
                        } else {
                            if (i == pipeList.length - 1) {
                                writeFile(filename, carriers);
                            }
                        }
                    }
                } else {
                    logger.warn("Empty name of pipe " + this.toString() + ". It hasn't been be saved.");
                }
            } else {
                for (i = step; i < pipeList.length; i++) {
                    p = pipeList[i];
                    if (p == null) {
                        logger.fatal("AbstractPipe " + i + " is null");
                        System.exit(-1);
                    } else {
                        p.pipeAll(carriers);
                    }
                }
            }

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SerialPipesSerializable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return carriers;
    }

    /**
     * Generate a md5 from a String
     *
     * @param name String name to generate a md5
     * @return a md5 from String
     */
    private String generateMD5(String name) {
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
            java.util.logging.Logger.getLogger(SerialPipesSerializable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

}
