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
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.bdp4j.util.PipeUtils;
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

                if (currentPipe instanceof ParallelPipes || currentPipe instanceof SerialPipes) {
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
                    boolean savedFile = false;
                    for (File listFile : listFiles) {
                        if (listFile.getPath().equals(pipeFilename)) {
                            savedFile = true;
                        }
                    }

                    if (savedFile) {
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
                                    // Combine properties and target
                                    if (currentPipe != null) {
                                        AbstractPipe currentPipeParent = currentPipe.getParent();
                                        Collection<Instance> savedCarriers;
                                        if (currentPipeParent instanceof ParallelPipes) {
                                            savedCarriers = (Collection<Instance>) combineInstances(currentPipeParent.getStorePath());
                                        } else {
                                            // Retrieve aditional data
                                            if (currentPipe instanceof SharedDataConsumer) {
                                                SharedDataConsumer currentDataConsumer = (SharedDataConsumer) currentPipe;
                                                currentDataConsumer.readFromDisk(PipeUtils.getSharedDataPath());
                                            }
                                            savedCarriers = (Collection<Instance>) PipeUtils.readFromDisk(pipeFilename);
                                        }
                                        // Retrieve carriers
                                        if (carriers.size() == savedCarriers.size()) {
                                            for (int x = 0; x < savedCarriers.size(); x++) {
                                                ((Instance) carriers.toArray()[x]).setData(((Instance) savedCarriers.toArray()[x]).getData());
                                            }
                                        } else {
                                            return this.pipeAll(carriers, step);
                                        }
                                    }
                                }
                            } else {
                                return this.pipeAll(carriers, 0);
                            }
                        }
                    } else {
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
     * Combine properties from saved instances in the indicated path and also
     * combine target.
     *
     * @param path Path name where instances to combine is saved
     * @return A collection of instances with all properties combined
     */
    private Collection<Instance> combineInstances(String path) {
        Collection<Instance> carriers = null;
        Collection<Instance> currentPipeCarriers;
        File directoryPath = new File(path);
        FileFilter filter = (File pathname) -> {
            return pathname.getPath().endsWith(".ser");
        };

        FileFilter filterFirst = (File pathname) -> {
            return pathname.getPath().endsWith(".ser") && pathname.getName().startsWith("0_");
        };
        File[] listSavedPipes = directoryPath.listFiles(filter);
        File[] firstPipe = directoryPath.listFiles(filterFirst);

        for (File pipeFilename : listSavedPipes) {
            if (carriers == null) {
                if (firstPipe.length > 0) {
                    carriers = (Collection<Instance>) PipeUtils.readFromDisk(firstPipe[0].getPath());
                } else {
                    carriers = (Collection<Instance>) PipeUtils.readFromDisk(pipeFilename.getPath());
                }
            } else {
                currentPipeCarriers = (Collection<Instance>) PipeUtils.readFromDisk(pipeFilename.getPath());

                for (int i = 0; i < carriers.size(); i++) {
                    Instance carrier = (Instance) carriers.toArray()[i];
                    Instance currentPipeCarrier = (Instance) currentPipeCarriers.toArray()[i];
                    Serializable currentPipeTarget = currentPipeCarrier.getTarget();
                    Serializable target = carrier.getTarget();
                    Set<String> currentPipePropertyList = currentPipeCarrier.getPropertyList();
                    // Retrieve target
                    if (target == null && currentPipeTarget != null) {
                        carrier.setTarget(currentPipeTarget);
                    } else if (target != null && currentPipeTarget != null) {
                        if (!carrier.getTarget().equals(currentPipeTarget)) {
                            logger.fatal("[COMBINE INSTANCES] Target instances doesn't match.");
                        }
                    }
                    for (String property : currentPipePropertyList) {
                        if (!carrier.hasProperty(property)) {
                            carrier.setProperty(property, (String) carrier.getProperty(property));
                        } else {
                            if (!carrier.getProperty(property).equals(currentPipeCarrier.getProperty(property))) {
                                logger.warn("[COMBINE INSTANCES] Property " + property + " has different values for the same instance.");
                            }
                        }
                    }
                }
            }
        }
        return carriers;
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
            boolean resumableMode = EBoolean.getBoolean(configurator.getProp(Configurator.RESUMABLE_MODE));
            String instancesFilePath = "";
            File instancesFile = null;
            if (resumableMode && !isDebuggingPipe() && step < pipes.size()) {
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

                    pipes.stream().parallel().forEach(
                            (p) -> {
                                if (pipes.indexOf(p) >= step) {
                                    p.pipeAll(carriers);

                                    // Save instances
                                    if (!p.isDebuggingPipe()) {
                                        String filename = p.getStorePath();
                                       if (p instanceof SerialPipes == false && p instanceof ParallelPipes == false) {
                                            PipeUtils.writeToDisk(filename, carriers);
                                        }
                                        // Save aditional data
                                        if (p instanceof SharedDataProducer) {
                                            SharedDataProducer currentDataProducer = (SharedDataProducer) p;
                                            currentDataProducer.writeToDisk(getPath(PipeUtils.getSharedDataPath()));
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
                            }
                    );
                } else {
                    logger.warn("Empty name of pipe " + this.toString() + ". It hasn't been be saved.");
                }
            } else {
                // Call pipeAll for each pipe included in the parallelPipes
                Collection<Instance> clones = new ArrayList<>();
                carriers.forEach((i) -> {
                    clones.add(new Instance(i));
                });
                Collection<Instance> ret;
                if (!resumableMode) {
                    ret = pipes.get(0).pipeAll(carriers);
                } else {
                    ret = clones;
                }
                pipes.stream().parallel().forEach(
                        (p) -> {
                            if (!p.equals(pipes.get(0)) && pipes.indexOf(p) >= step) {
                                Collection<Instance> clones2 = new ArrayList<>();
                                for (Instance i : carriers) {
                                    clones2.add(new Instance(i));
                                }
                                clones2 = p.pipeAll(clones2);

                                // Copy the target if required
                                if (((ArrayList<Instance>) clones2).get(0).getTarget() != null) {
                                    for (int i = 0; i < clones2.size(); i++) {
                                        Serializable target = ((ArrayList<Instance>) clones2).get(i).getTarget();
                                        if (target == null) {
                                            logger.fatal("Instance with no target: " + ((ArrayList<Instance>) clones2).get(i).getName());
                                            System.exit(0);
                                        }
                                        ((ArrayList<Instance>) ret).get(i).setTarget(target);
                                    }
                                }
                            }
                        }
                );
                return ret;
            }

        } catch (Exception ex) {
            logger.warn(" [ " + ResumableParallelPipes.class.getName() + " ] " + ex.getMessage());
        }
        return carriers;
    }
}
