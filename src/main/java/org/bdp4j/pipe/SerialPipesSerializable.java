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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import java.security.*;
import java.util.Base64;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.EBoolean;

/**
 * Convert an instance through a sequence of pipes.
 *
 * The configuration of the pipe (including the temporal folder, the debug
 * mode...) is created using the last used Configurator
 * (Configurator.getLastUsed()). If another configuration is required, please
 * stablish it through apropiate setters.
 *
 * @author María Novo
 */
public class SerialPipesSerializable extends SerialPipes {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(SerialPipes.class);

    /**
     * Default configuration
     */
    private Configurator configurator = Configurator.getLastUsed();

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
     * Set the configuration
     *
     * @param configurator The configuration to use.
     */
    @PipeParameter(name = "configurator", description = "The configuration to use", defaultValue = "")
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    /**
     * Get the configuration
     *
     * @return The used configuration.
     */
    public Configurator getConfigurator() {
        return this.configurator;
    }

    /**
     * Saved data in a file
     *
     * @param filename File name where the data is saved
     * @param carriers Data to save
     */
    public void saveData(String filename, Object carriers) {
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
    public Object retrieveData(String filename) {
        File file = new File(filename);
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            ObjectInputStream input = new ObjectInputStream(buffer);

            return input.readObject();

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SerialPipesSerializable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Checks if pipes that are annotated as DataReaderPipe or DataWriterPipe
     * implements the corresponding interface.
     *
     * @param pipeList List of pipes to check
     * @return False if any pipe annotated as DataReaderPipe or
     * DataWriterPipe don't implement the corresponding interface.True in
     * any other case.
     *
     */
    public boolean checkDataManager(AbstractPipe[] pipeList) {

        Class<?> abstractPipeClass = null;
        boolean returnValueReader = false;
        boolean returnValueWriter = false;
        String errorReader = "";
        String errorWriter = "";

        for (AbstractPipe abstractPipe : pipeList) {
            abstractPipeClass = abstractPipe.getClass();
            // Reader
            if (abstractPipeClass.getAnnotationsByType(DataReaderPipe.class).length > 0) {
                returnValueReader = false;
                errorReader = "Pipe " + abstractPipeClass.getSimpleName() + " has to implement DataReader interface.";
                Class<?>[] interfaces = abstractPipeClass.getInterfaces();
                for (Class<?> aInterface : interfaces) {
                    if (aInterface.equals(DataReader.class)) {
                        returnValueReader = true;
                    }
                }
                if (!returnValueReader) {
                    logger.error(errorReader);
                    return false;
                }

            } else {
                returnValueReader = true;
            }
            // Writer
            if (returnValueReader == true) {
                returnValueWriter = false;
                if (abstractPipeClass.getAnnotationsByType(DataWriterPipe.class).length > 0) {
                    errorWriter = "Pipe " + abstractPipeClass.getSimpleName() + " has to implement DataWriter interface.";
                    Class<?>[] interfaces = abstractPipeClass.getInterfaces();
                    for (Class<?> aInterface : interfaces) {
                        if (aInterface.equals(DataWriter.class)) {
                            returnValueWriter = true;
                        }
                    }
                    if (!returnValueWriter) {
                        logger.error(errorWriter);
                        return false;
                    }
                } else {
                    returnValueWriter = true;
                }
            }
        }
        if (!errorReader.equals("")) {
            logger.error(errorReader);
        }
        if (!errorWriter.equals("")) {
            logger.error(errorWriter);
        }
        return returnValueReader && returnValueWriter;
    }

    /**
     * AbstractPipe a collection of instances through the whole process. In
     * addiction, it calculates the step to continue execution, depending on the
     * configuration.
     *
     * @param carriers The instances to be processed
     * @return the instances after processing them
     */
    @Override
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        int step = 0;
        boolean serializableMode = EBoolean.getBoolean(configurator.getProp(Configurator.SERIALIZABLE_MODE));
        if (serializableMode) {
            // Calculate pipe to continue execution
            AbstractPipe[] pipeList = super.getPipes();
            if (!checkDataManager(pipeList)) {
                System.exit(0);
            }
            String md5PipeName = generateMD5(this.toString());
            File sourcePath = new File(configurator.getProp(Configurator.TEMP_FOLDER) + "/" + md5PipeName);
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
                // If exists check if file with instances matches with md5PipeName.txt
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

                        String deserializedCarriers = (String) retrieveData(sourcePath + "/" + md5PipeName + ".txt");
                        // If instances match, the pipe and instances are the same, so, this is the first step
                        if (deserializedCarriers.equals(md5Carriers.toString())) {
                            String[] pipeIndex = filename.split("_");
                            step = Integer.parseInt(pipeIndex[0]) + 1;
                            Collection<Instance> instances = (Collection<Instance>) retrieveData(sourcePath + "/" + filename);
                            return this.pipeAll(instances, step);
                        }
                    }
                }
            }
        }
        return this.pipeAll(carriers, step);
    }

    /**
     * AbstractPipe a collection of instances through the whole process, from de
     * defined step and save this, depending of the configuration.
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
            boolean serializableMode = EBoolean.getBoolean(configurator.getProp(Configurator.SERIALIZABLE_MODE));
            boolean debugMode = EBoolean.getBoolean(configurator.getProp(Configurator.DEBUG_MODE));
            String temp_folder = configurator.getProp(Configurator.TEMP_FOLDER);

            if (serializableMode) {
                File sourcePath = new File(temp_folder);
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

                    File path = new File(temp_folder + "/" + md5PipeName + "/");
                    if (!path.exists()) {
                        path.mkdir();
                    }

                    // Create file with md5Carrier
                    if (sourcePath.exists() && sourcePath.isDirectory()) {
                        File instancesFile = new File(path.getPath() + "/" + md5PipeName + ".txt");
                        saveData(instancesFile.getPath(), md5Carriers.toString());
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
                        if (debugMode) {
                            saveData(filename, carriers);
                        } else {
                            if (i == pipeList.length - 1) {
                                saveData(filename, carriers);
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
