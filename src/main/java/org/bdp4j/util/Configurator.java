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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.ParallelPipes;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.types.PipeType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Singleton configurator class for configuring the app and pipe from de
 * configuration file.
 *
 * @author Yeray Lage
 */
public final class Configurator {
    /**
     * Default samples folder property key.
     */
    public static final String SAMPLES_FOLDER = "samplesFolder";

    /**
     * Default samples folder property value.
     */
    public static final String DEFAULT_SAMPLES_FOLDER = "./samples";

    /**
     * Default plugins folder property key.
     */
    public static final String PLUGINS_FOLDER = "pluginsFolder";

    /**
     * Default plugins folder property value.
     */
    public static final String DEFAULT_PLUGINS_FOLDER = "./plugins";

    /**
     * Default output folder property key.
     */
    public static final String OUTPUT_FOLDER = "outputFolder";

    /**
     * Default output folder property value.
     */
    public static final String DEFAULT_OUTPUT_FOLDER = "./output";

    /**
     * Default tmp folder property key.
     */
    public static final String TEMP_FOLDER = "tempFolder";

    /**
     * Default tmp folder property value.
     */
    public static final String DEFAULT_TEMP_FOLDER = System.getProperty("java.io.tmpdir");

    /**
     * Default debug mode property key.
     */
    public static final String DEBUG_MODE = "debug";

    /**
     * Default debug mode property value.
     */
    public static final String DEFAULT_DEBUG_MODE = "no";

    /**
     * Default resumable mode property key.
     */
    public static final String RESUMABLE_MODE = "resumable";

    /**
     * Default resumable mode property value.
     */
    public static final String DEFAULT_RESUMABLE_MODE = "no";

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Configurator.class);

    /**
     * The default configuration file.
     */
    private static final String DEFAULT_CONFIG_PATH = "./config/configuration.xml";

    /**
     * The global properties.
     */
    private HashMap<String, String> props;

    /**
     * The XML document.
     */
    private Document document;

    /**
     * The available pipes.
     */
    private HashMap<String, PipeInfo> pipes;

    /**
     * The configurations
     */
    private static HashMap<String, Configurator> configurations = new HashMap<>();

    /**
     * The last used configuration
     */
    private static Configurator lastUsed = null;

    /**
     * Detailed information about irrecoverable error
     */
    private static String irrecoverableErrorInfo = "";

    /**
     * The action on errors
     */
    private static Runnable actionOnIrrecoverableError = new Runnable() {
        @Override
        public void run() {
            logger.fatal("Application should be stopped: "+ getIrrecoverableErrorInfo() );
            System.exit(-1);
        }
    };

    /**
     * Default constructor
     */
    private Configurator() {
        props = new HashMap<>();
        this.setProp(SAMPLES_FOLDER, DEFAULT_SAMPLES_FOLDER);
        this.setProp(PLUGINS_FOLDER, DEFAULT_PLUGINS_FOLDER);
        this.setProp(OUTPUT_FOLDER, DEFAULT_OUTPUT_FOLDER);
        this.setProp(TEMP_FOLDER, DEFAULT_TEMP_FOLDER);
        this.setProp(DEBUG_MODE, DEFAULT_DEBUG_MODE);
        this.setProp(RESUMABLE_MODE, DEFAULT_RESUMABLE_MODE);
    }

    /**
     * Returns the information about an irrecoverable error
     * @return the information about an irrecoverable error that recently happened 
     */
    public static String getIrrecoverableErrorInfo() {
        return irrecoverableErrorInfo;
    }

    /**
     * Stablish the information about an irrecoveriable error
     * @param irrecoverableErrorInfo The detailed information about the irrecoverable error
     */
    public static void setIrrecoverableErrorInfo(String irrecoverableErrorInfo) {
        Configurator.irrecoverableErrorInfo = irrecoverableErrorInfo;
    }

    /**
     * Returns a callback defining how should be done when an error is found
     * 
     * @return The callback defining how shoudl be done on a irrecoverable error
     */
    public static Runnable getActionOnIrrecoverableError() {
        return actionOnIrrecoverableError;
    }

    /**
     * Stablishes what to do when an error is found
     * @param actionOnError A runnable speficiying the actions to do on a irrecoverable error
     */
    public static void setActionOnIrrecoverableError(Runnable actionOnError) {
        Configurator.actionOnIrrecoverableError = actionOnError;
    }

    /**
     * Empty constructor that initializes the props HashMap and instantiates the
     * document from the config file.
     */
    private Configurator(String configPath) {
        this();
        try {
            File file = new File(configPath);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();
        } catch (Exception e) {
            logger.error("[CONFIGURATOR] Error loading file.");
        }
    }

    /**
     * Singleton instance getter. If configPath is null (or void string),then
     * the default configuration parameters are loaded in the configuration
     *
     * @param configPath Path to the configuration file.
     * @return The singleton instance.
     */
    public static Configurator getInstance(String configPath) {
        String requestedConfiguration = (configPath == null) ? "" : configPath;
        Configurator returnValue = configurations.get(requestedConfiguration);

        if (requestedConfiguration.equals("")) {
            if (configPath == null) {
                returnValue = new Configurator();
            } else {
                returnValue = new Configurator(configPath);
            }
        } else if (returnValue == null) {
            if (configPath == null) {
                returnValue = new Configurator();
            } else {
                returnValue = new Configurator(configPath);
            }
        }

        lastUsed = returnValue;
        return returnValue;
    }

    /**
     * Returns the last configuration used
     *
     * @return The last used configuration
     */
    public static Configurator getLastUsed() {
        if (lastUsed==null){
            lastUsed = new Configurator();
        }
        return lastUsed;
    }

    /**
     * This method gets all the properties defined on the configuration file
     */
    public void configureApp() {
        // Node children from the general element (all the properties).
        NodeList generalChildren = document.getElementsByTagName("general").item(0).getChildNodes();

        for (int i = 0; i < generalChildren.getLength(); i++) {
            // For each property defined on the general configuration.
            Node property = generalChildren.item(i);

            if (!generalChildren.item(i).getNodeName().contains("#")) {
                logger.info("[PROPERTIES LOAD] " + property.getNodeName() + " -> " + property.getTextContent().trim());
                props.put(property.getNodeName(), property.getTextContent().trim());
            }
        }
    }

    /**
     * This method sets the pipe with the defined structure
     *
     * @param availablePipes All the available pipes
     * @return Configured pipe with the available ones and the defined
     * structure.
     */
    public Pipe configurePipeline(HashMap<String, PipeInfo> availablePipes) {
        pipes = availablePipes;
        AbstractPipe configuredPipe = null;

        // Full pipeStructure
        Node pipeStructure = document.getElementsByTagName("pipeline").item(0);

        // Temp attributes properties
        props.put(RESUMABLE_MODE, pipeStructure.getAttributes().getNamedItem(RESUMABLE_MODE).getNodeValue());
        props.put(DEBUG_MODE, pipeStructure.getAttributes().getNamedItem(DEBUG_MODE).getNodeValue());

        // Global pipe (serialPipe or parallelPipe)
        Node globalPipe = null;
        for (int x = 0; x < pipeStructure.getChildNodes().getLength(); x++) {
            if (!pipeStructure.getChildNodes().item(x).getNodeName().contains("#")) {
                globalPipe = pipeStructure.getChildNodes().item(x);
            }
        }
        assert globalPipe != null;
        if (globalPipe.getNodeName().equals("serialPipes")) {
            configuredPipe = new SerialPipes();
        } else if (globalPipe.getNodeName().equals("parallelPipes")) {
            configuredPipe = new ParallelPipes();
        } else {
            logger.fatal("[PIPE CONFIGURATION] No serialPipe or parallelPipe is correctly defined.");
            Configurator.setIrrecoverableErrorInfo("[PIPE CONFIGURATION] No serialPipe or parallelPipe is correctly defined.");
            Configurator.getActionOnIrrecoverableError().run();
        }

        // Global pipe children
        NodeList globalPipeChildren = globalPipe.getChildNodes();

        // Now we iterate over the global pipe children
        for (int i = 0; i < globalPipeChildren.getLength(); i++) {
            if (!globalPipeChildren.item(i).getNodeName().contains("#")) {
                Node child = globalPipeChildren.item(i);

                if (child.getNodeName().equals("serialPipes")) {
                    // AbstractPipe is serialPipes
                    if (configuredPipe instanceof SerialPipes) {
                        ((SerialPipes) configuredPipe).add(pipesFromSerial(child));
                    } else {
                        ((ParallelPipes) configuredPipe).add(pipesFromSerial(child));
                    }
                } else if (child.getNodeName().equals("parallelPipes")) {
                    // AbstractPipe is parallelPipes
                    if (configuredPipe instanceof SerialPipes) {
                        ((SerialPipes) configuredPipe).add(pipesFromParallel(child));
                    } else {
                        ((ParallelPipes) configuredPipe).add(pipesFromParallel(child));
                    }
                } else {
                    // AbstractPipe is a type of processing pipe
                    try {
                        if (configuredPipe instanceof SerialPipes) {
                            ((SerialPipes) configuredPipe).add(getPipeInstance(child.getTextContent().trim(), child));
                        } else {
                            ((ParallelPipes) configuredPipe).add(getPipeInstance(child.getTextContent().trim(), child));
                        }
                    } catch (NullPointerException e) {
                        logger.fatal("[PIPE CONFIGURATION] " + child.getTextContent().trim()
                                + " does not exist or is not loaded.");
                        Configurator.setIrrecoverableErrorInfo("[PIPE CONFIGURATION] " + child.getTextContent().trim()
                        + " does not exist or is not loaded.");
                        Configurator.getActionOnIrrecoverableError().run();
                    }
                }
            }
        }

        if (configuredPipe.countPipes(PipeType.TARGET_ASSIGNING_PIPE) > 1) {
            logger.fatal("[PIPE CONFIGURATION] The number of target assigning pipes must be one or zero.");
            Configurator.setIrrecoverableErrorInfo("[PIPE CONFIGURATION] The number of target assigning pipes must be one or zero.");
            Configurator.getActionOnIrrecoverableError().run();
        }

        return configuredPipe;
    }

    /**
     * Returns the pipe if exists, and sets
     *
     * @param pipeName Name of the pipe.
     * @return Instance of the pipe.
     */
    private AbstractPipe getPipeInstance(String pipeName, Node pipeNode) {
        AbstractPipe pipe = null;
        pipeName = pipeName.split("\n")[0];
        boolean pipeIsDebug = false;

        if (pipes.get(pipeName) == null) {
            logger.fatal("[PIPE GET] " + pipeName + " is not loaded.");
            Configurator.setIrrecoverableErrorInfo("[PIPE GET] " + pipeName + " is not loaded.");
            Configurator.getActionOnIrrecoverableError().run();
        } else {
            PipeInfo pipeInfo = pipes.get(pipeName);

            // We check the pipeParameters from configuration file
            NodeList pipeNodeChildren = pipeNode.getChildNodes();


            for (int i = 0; i < pipeNodeChildren.getLength(); i++) {
                if (!pipeNodeChildren.item(i).getNodeName().contains("#")) {
                    if (pipeNodeChildren.item(i).getNodeName().equals("params")) {
                        NodeList params = pipeNodeChildren.item(i).getChildNodes();
                        for (int j = 0; j < params.getLength(); j++) {
                            if (!params.item(j).getNodeName().contains("#")) {
                                if (params.item(j).getNodeName().equals("pipeParameter")) {
                                    NodeList pipeParameterValues = params.item(j).getChildNodes();
                                    String pipeParameterName = null;
                                    for (int k = 0; k < pipeParameterValues.getLength(); k++) {
                                        if (!pipeParameterValues.item(k).getNodeName().contains("#")) {
                                            if (pipeParameterValues.item(k).getNodeName().equals("name")) {
                                                pipeParameterName = pipeParameterValues.item(k).getTextContent();
                                            }
                                            if (pipeParameterValues.item(k).getNodeName().equals("value")) {
                                                String value = pipeParameterValues.item(k).getTextContent();
                                                pipeInfo.setPipeParam(pipeParameterName, value);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (pipeNodeChildren.item(i).getNodeName().equals("debug")) {
                        pipeIsDebug = true;
                    }
                }
            }

            try {
                pipe = (AbstractPipe) pipes.get(pipeName).getPipeClass().newInstance();

                Method[] methods = pipe.getClass().getMethods();
                for (Method m : methods) {
                    if (m.getAnnotation(PipeParameter.class) != null) {
                        PipeParameter pipeParameter = m.getAnnotation(PipeParameter.class);
                        String value = pipeInfo.getPipeParams().get(pipeParameter.name()).getValue();
                        if (value != null) {
                            m.invoke(pipe, value);
                        }
                    }
                }
            } catch (Exception e) {
                logger.fatal("[GET PIPE INSTANCE] Error getting pipe instance of " + pipeName);
                Configurator.setIrrecoverableErrorInfo("[GET PIPE INSTANCE] Error getting pipe instance of " + pipeName);
                Configurator.getActionOnIrrecoverableError().run();
            }
        }

        if (pipeIsDebug) pipe.setDebugging(true);

        return pipe;
    }

    /**
     * Get the serialPipes completely formed from the node.
     *
     * @param serialPipesNode SerialPipes node.
     * @return SerialPipes formed.
     */
    private SerialPipes pipesFromSerial(Node serialPipesNode) {
        SerialPipes serialPipes = new SerialPipes();
        NodeList children = serialPipesNode.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!child.getNodeName().contains("#")) {
                if (child.getNodeName().equals("serialPipes")) {
                    serialPipes.add(pipesFromSerial(child));
                } else if (child.getNodeName().equals("parallelPipes")) {
                    serialPipes.add(pipesFromParallel(child));
                } else {
                    serialPipes.add(getPipeInstance(getNameFromPipe(child), child));
                }
            }
        }

        return serialPipes;
    }

    /**
     * Get the parallelPipes completely formed from the node.
     *
     * @param parallelPipesNode ParallelPipes node.
     * @return ParallelPipes formed.
     */
    private ParallelPipes pipesFromParallel(Node parallelPipesNode) {
        ParallelPipes parallelPipes = new ParallelPipes();
        NodeList children = parallelPipesNode.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (!child.getNodeName().contains("#")) {
                if (child.getNodeName().equals("serialPipes")) {
                    parallelPipes.add(pipesFromSerial(child));
                } else if (child.getNodeName().equals("parallelPipes")) {
                    parallelPipes.add(pipesFromParallel(child));
                } else {
                    parallelPipes.add(getPipeInstance(getNameFromPipe(child), child));
                }
            }
        }

        return parallelPipes;
    }

    /**
     * Get the name of a pipe node.
     *
     * @param pipe Node pipe.
     * @return The name of the pipe.
     */
    private String getNameFromPipe(Node pipe) {
        String name = null;
        NodeList children = pipe.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals("name")) {
                name = child.getTextContent();
                return name;
            }
        }

        logger.fatal("[GET NAME FROM PIPE] Could not get name from pipe.");
        Configurator.setIrrecoverableErrorInfo("[GET NAME FROM PIPE] Could not get name from pipe.");
        Configurator.getActionOnIrrecoverableError().run();
        return name;
    }

    /**
     * Gets the property from the one defined on the configuration file.
     *
     * @param k Key (name of the property).
     * @return The value of the property.
     */
    public String getProp(String k) {
        // Check if the property exists.
        if (props.get(k) == null) {
            logger.fatal("[PROPERTY GET] The requested property '" + k + "' does not exists.");
            Configurator.setIrrecoverableErrorInfo("[PROPERTY GET] The requested property '" + k + "' does not exists.");
            Configurator.getActionOnIrrecoverableError().run();
        }

        return props.get(k);
    }

    /**
     * Sets the property.
     *
     * @param k Key (name of property).
     * @param v Value
     */
    public void setProp(String k, String v) {
        props.put(k, v);
    }

}
