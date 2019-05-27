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

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.util.Configurator;
import org.bdp4j.util.EBoolean;
import org.bdp4j.util.PipeUtils;

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
public class ResumableSerialPipes extends SerialPipes {

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
    public ResumableSerialPipes() {
        super();
    }

    /**
     * Build an empty SerialPipes
     *
     * @param pipes The pipes that included in the SerialPipe
     */
    public ResumableSerialPipes(AbstractPipe[] pipes) {
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

        boolean resumableMode = EBoolean.getBoolean(configurator.getProp(Configurator.RESUMABLE_MODE));
        if (resumableMode) {

            // Calculate pipe to continue execution
            AbstractPipe[] pipeList = super.getPipes();
            for (step = 0; step < pipeList.length; step++) {
                AbstractPipe currentPipe = pipeList[step];

                if (currentPipe != null && currentPipe.isDebuggingPipe()) {
                    return this.pipeAll(carriers, step);
                }

                if (currentPipe instanceof SerialPipes || currentPipe instanceof ParallelPipes) {
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
                File f = new File("x");

                if (sourcePath.exists() && sourcePath.isDirectory() && listFiles.length > 0) {
                    Arrays.sort(sourcePath.listFiles(), (File f1, File f2) -> Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()));
                    String pipeFilename = ((currentPipe != null) ? currentPipe.getStorePath() : "");
                    String lastModifiedFile = listFiles[0].getPath();
                    int lastModifiedFileStep = Integer.parseInt(listFiles[0].getName().split("_")[0]);

                    if (lastModifiedFile.equals(pipeFilename) && lastModifiedFileStep == step) {
                        // Check if instances(carriers) matches
                        StringBuilder md5Carriers = new StringBuilder();
                        carriers.stream().map((carrier) -> PipeUtils.generateMD5(carrier.toString())).forEachOrdered((md5Carrier) -> {
                            md5Carriers.append(md5Carrier);
                        });
                        String instancesFileName = getStorePath() + sourcePath.getName() + ".txt";
                        File instancesFile = new File(instancesFileName);
                        if (sourcePath.exists() && sourcePath.isDirectory()) {
                            if (instancesFile.exists()) {
                                String deserializedCarriers = (String) PipeUtils.readFromDisk(getStorePath() + sourcePath.getName() + ".txt");
                                // If instances match, the pipe and instances are the same, so, this is the first step
                                if (!deserializedCarriers.equals(md5Carriers.toString())) {

                                    return this.pipeAll(carriers, step);
                                } else {
                                    // Retrieve aditional data
                                    if (currentPipe instanceof SharedDataConsumer) {
                                        SharedDataConsumer currentDataConsumer = (SharedDataConsumer) currentPipe;
                                        currentDataConsumer.readFromDisk(PipeUtils.getSharedDataPath());
                                    }
                                    // Retrieve carriers
                                    Collection<Instance> savedCarriers = (Collection<Instance>) PipeUtils.readFromDisk(pipeFilename);
                                    if (carriers.size() == savedCarriers.size()) {
                                        for (int x = 0; x < savedCarriers.size(); x++) {
                                            ((Instance) carriers.toArray()[x]).setData(((Instance) savedCarriers.toArray()[x]).getData());
                                        }
                                    } else {
                                        return this.pipeAll(carriers, step);
                                    }
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
            AbstractPipe p = null;
            AbstractPipe[] pipeList = super.getPipes();
            boolean resumableMode = EBoolean.getBoolean(configurator.getProp(Configurator.RESUMABLE_MODE));
            boolean debugMode = EBoolean.getBoolean(configurator.getProp(Configurator.DEBUG_MODE));
            String instancesFilePath = "";
            File instancesFile = null;
            if (resumableMode && !isDebuggingPipe() && step < pipeList.length) {
                String md5PipeName = getStorePath();
                if (!md5PipeName.equals("")) {
                    // Generate MD5 to carriers
                    StringBuilder md5Carriers = new StringBuilder();
                    carriers.stream().map((carrier) -> PipeUtils.generateMD5(carrier.toString())).forEachOrdered((md5Carrier) -> {
                        md5Carriers.append(md5Carrier);
                    });

                    if (!isDebuggingPipe()) {
                        File instancesFileName = new File(getStorePath());
                        if (instancesFileName.exists() && instancesFileName.isDirectory()) {
                            instancesFilePath = getStorePath() + instancesFileName.getName() + ".txt";
                            instancesFile = new File(instancesFilePath);
                            if (!instancesFile.exists()) {
                                PipeUtils.writeToDisk(instancesFile.getPath(), md5Carriers.toString());
                            }
                        }
                    }
                    for (i = step; i < pipeList.length; i++) {
                        p = pipeList[i];

                        if (p == null) {
                            logger.fatal("AbstractPipe " + i + " is null");
                            System.exit(-1);
                        } else {
                            p.pipeAll(carriers);
                        }

                        // Save instances
                        if (!p.isDebuggingPipe()) {

                            String filename = p.getStorePath();
                            if (debugMode) {
                                if (p instanceof SerialPipes == false && p instanceof ParallelPipes == false) {
                                    PipeUtils.writeToDisk(filename, carriers);
                                }
                                // Save aditional data
                                if (p instanceof SharedDataProducer) {
                                    SharedDataProducer currentDataProducer = (SharedDataProducer) p;
                                    currentDataProducer.writeToDisk(getPath(PipeUtils.getSharedDataPath()));
                                }
                            } else {
                                if (i == pipeList.length - 1) {
                                    if (p instanceof SerialPipes == false && p instanceof ParallelPipes == false) {
                                        PipeUtils.writeToDisk(filename, carriers);
                                    }
                                    // Save aditional data
                                    if (p instanceof SharedDataProducer) {
                                        SharedDataProducer currentDataProducer = (SharedDataProducer) p;
                                        currentDataProducer.writeToDisk(PipeUtils.getSharedDataPath());
                                    }
                                }
                            }
                        }
                        File f = new File(md5PipeName);
                        if (f.exists() && f.listFiles().length == 1 && f.listFiles()[0].getPath().equals(instancesFilePath)) {
                            if (instancesFile != null && instancesFile.exists()) {
                                instancesFile.delete();
                            }
                            f.delete();
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
            logger.warn(" [ " + ResumableSerialPipes.class.getName() + " ] " + ex.getMessage());
        }
        return carriers;
    }
}
