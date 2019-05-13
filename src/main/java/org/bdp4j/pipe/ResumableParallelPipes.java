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
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.logging.Level;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.EBoolean;

/**
 * A class implementing parallel processing of instances
 *
 * @author Yeray Lage
 */
public class ResumableParallelPipes extends ParallelPipes {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(ResumableParallelPipes.class);

    /**
     * Default configuration
     */
    private Configurator configurator = Configurator.getLastUsed();

    /**
     * The input type
     */
    private Class<?> inputType = null;

    /**
     * The output type
     */
    private Class<?> outputType = null;

    /**
     * Pipes being executed in parallel
     */
    private ArrayList<AbstractPipe> pipes;

    /**
     * Default constructor, initializes the arrayList.
     */
    public ResumableParallelPipes() {
        super();
    }

    /**
     * Constructor that initializes the parallelPipes array and add pipes to it.
     *
     * @param pipeList The array of pipes to be included in the parallelPipe.
     */
    public ResumableParallelPipes(AbstractPipe[] pipeList) {
        super(pipeList);
        this.pipes = new ArrayList<>(Arrays.asList(pipeList));
    }

    /**
     * Constructor that initializes the parallelPipes array and add pipes to it.
     *
     * @param pipeList The ArrayList of pipes to be included in the
     * parallelPipe.
     */
    public ResumableParallelPipes(ArrayList<AbstractPipe> pipeList) {
        super(pipeList);
        this.pipes = new ArrayList<>(pipeList);

    }

    /**
     * Pipe a collection of instances through the whole process.
     *
     * @param carriers Collection of instances to pipe.
     * @return Collection of instances after being processed.
     */
    @Override
    public Collection<Instance> pipeAll(Collection<Instance> carriers) {
        int step = 0;

        boolean resumableMode = EBoolean.getBoolean(configurator.getProp(Configurator.RESUMABLE_MODE));
        if (resumableMode) {

            // Calculate pipe to continue execution
            AbstractPipe[] pipeList = super.getPipes();
            for (step = 0; step < pipeList.length; step++) {
                AbstractPipe currentPipe = pipeList[step];

                if (currentPipe != null && currentPipe.isDebuggingPipe()) {
                    return this.pipeAll(carriers, step);
                }

                if (currentPipe instanceof ParallelPipes) {
                    currentPipe.pipeAll(carriers);
                    step = this.findPosition(currentPipe) + 1;
                    break;
                }

                File sourcePath = new File(getStorePath());
                // Get all .ser files
                FileFilter filter;
                filter = (File pathname) -> {
                    return pathname.getPath().endsWith(".ser");
                };
                // Get saved list of files
                File[] listFiles = sourcePath.listFiles(filter);
                if (sourcePath.exists() && sourcePath.isDirectory() && listFiles.length > 0) {
                    Arrays.sort(sourcePath.listFiles(), (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));

                    String pipeFilename = ((currentPipe != null) ? currentPipe.getStorePath() : "");
                    String lastModifiedFile = listFiles[listFiles.length - 1].getPath();
                    int lastModifiedFileStep = Integer.parseInt(listFiles[listFiles.length - 1].getName().split("_")[0]);

                    if (lastModifiedFile.equals(pipeFilename) && lastModifiedFileStep == step) {
                        // Check if instances(carriers) matches
                        StringBuilder md5Carriers = new StringBuilder();
                        carriers.stream().map((carrier) -> generateMD5(carrier.toString())).forEachOrdered((md5Carrier) -> {
                            md5Carriers.append(md5Carrier);
                        });
                        String instancesFileName = getStorePath() + sourcePath.getName() + ".txt";
                        File instancesFile = new File(instancesFileName);
                        if (sourcePath.exists() && sourcePath.isDirectory()) {
                            if (instancesFile.exists()) {
                                String deserializedCarriers = (String) readFromDisk(getStorePath() + sourcePath.getName() + ".txt");

                                // If instances match, the pipe and instances are the same, so, this is the first step
                                if (!deserializedCarriers.equals(md5Carriers.toString())) {

                                    return this.pipeAll(carriers, step);
                                } else {
                                    carriers = (Collection<Instance>) readFromDisk(pipeFilename);
                                }
                            } else {
                                return this.pipeAll(carriers, 0);
                            }
                        }
                    } else if (lastModifiedFileStep < step && !lastModifiedFile.equals(pipeFilename)) {
                        return this.pipeAll(carriers, step);
                    }
                } else {
                    return this.pipeAll(carriers, 0);
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

            boolean resumableMode = EBoolean.getBoolean(configurator.getProp(Configurator.RESUMABLE_MODE));
            boolean debugMode = EBoolean.getBoolean(configurator.getProp(Configurator.DEBUG_MODE));
            String instancesFilePath = "";
            File instancesFile = null;
            System.out.println("pipe " + this.toString());
            if (resumableMode && !isDebuggingPipe() && step < pipes.size()) {
                String md5PipeName = getStorePath();
                if (!md5PipeName.equals("")) {
                    // Generate MD5 to carriers
                    StringBuilder md5Carriers = new StringBuilder();
                    carriers.stream().map((carrier) -> generateMD5(carrier.toString())).forEachOrdered((md5Carrier) -> {
                        md5Carriers.append(md5Carrier);
                    });

                    if (!isDebuggingPipe()) {
                        File instancesFileName = new File(getStorePath());
                        if (instancesFileName.exists() && instancesFileName.isDirectory()) {
                            instancesFilePath = getStorePath() + instancesFileName.getName() + ".txt";
                            instancesFile = new File(instancesFilePath);
                            if (!instancesFile.exists()) {
                                writeToDisk(instancesFile.getPath(), md5Carriers.toString());
                            }
                        }
                    }

                    pipes.stream().parallel().forEach(
                            (p) -> {
                                p.pipeAll(carriers);

                                // Save instances
                                if (!p.isDebuggingPipe()) {

                                    String filename = p.getStorePath();
                                    if (debugMode) {
                                        writeToDisk(filename, carriers);
                                    } else {
                                        if (i == pipes.size() - 1) {
                                            writeToDisk(filename, carriers);
                                        }
                                    }
                                }
                                File f = new File(md5PipeName);
                                File fGetStorePath = new File(getStorePath());
                                String iFilePath = getStorePath() + fGetStorePath.getName() + ".txt";
                                File iFile = new File(iFilePath);
                                if (f.exists() && f.listFiles().length == 1 && f.listFiles()[0].getPath().equals(iFilePath)) {
                                    if (iFile.exists()) {
                                        iFile.delete();
                                    }
                                    f.delete();
                                }
                            }
                    );
                } else {
                    logger.warn("Empty name of pipe " + this.toString() + ". It hasn't been be saved.");
                }
            } else {
                pipes.stream().parallel().forEach(
                        (p) -> {
                            if (p == null) {
                                logger.fatal("AbstractPipe is null");
                                System.exit(-1);
                            } else {
                                p.pipeAll(carriers);
                            }
                        }
                );
            }

        } catch (Exception ex) {
            logger.warn(" [ " + ResumableSerialPipes.class.getName() + " ] " + ex.getMessage());
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
            java.util.logging.Logger.getLogger(ResumableSerialPipes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * Saved data in a file
     *
     * @param filename File name where the data is saved
     * @param carriers Data to save
     */
    public void writeToDisk(String filename, Object carriers) {
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

    /**
     * Retrieve data from file
     *
     * @param filename File name to retrieve data
     * @return an Object with the deserialized retrieve data
     */
    public Object readFromDisk(String filename) {
        File file = new File(filename);
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            ObjectInputStream input = new ObjectInputStream(buffer);

            return input.readObject();

        } catch (Exception ex) {
            logger.error("[READ FROM DISK] " + ex.getMessage());
        }
        return null;
    }

}
