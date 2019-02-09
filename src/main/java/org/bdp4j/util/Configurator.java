package org.bdp4j.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.ParallelPipes;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.SerialPipes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;

/**
 * Singleton configurator class for configure the app and pipe from de configuration file.
 *
 * @author Yeray Lage
 */
public class Configurator {
    private static final Logger logger = LogManager.getLogger(Configurator.class);
    private static Configurator ourInstance = new Configurator();
    private HashMap<String, String> props;
    private Document document;
    private HashMap<String, Pipe> pipes;

    /**
     * Empty constructor that initializes the props HashMap and instantiates the document from the config file.
     */
    private Configurator() {
        props = new HashMap<>();

        try {
            File file = new File("./config/configuration.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();
        } catch (Exception e) {
            logger.error("[CONFIGURATOR] Error loading file.");
        }
    }

    /**
     * Singleton instance getter.
     *
     * @return The singleton instance.
     */
    public static Configurator getInstance() {
        return ourInstance;
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
                logger.info("[PROPERTIES LOAD] " + property.getNodeName() + " -> " + property.getTextContent().trim()
                        + ".");
                props.put(property.getNodeName(), property.getTextContent().trim());
            }
        }
    }

    /**
     * This method sets the pipe with the defined structure
     *
     * @param availablePipes All the available pipes
     * @return Configured pipe with the available ones and the defined structure.
     */
    public Pipe configurePipe(HashMap<String, Pipe> availablePipes) {
        pipes = availablePipes;
        Pipe configuredPipe = null;

        // Full pipeStructure
        Node pipeStructure = document.getElementsByTagName("pipeStructure").item(0);

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
            logger.error("[PIPE CONFIGURATION] No serialPipe or parallelPipe is correctly defined.");
            System.exit(-1);
        }

        // Global pipe children
        NodeList globalPipeChildren = globalPipe.getChildNodes();

        // Now we iterate over the global pipe children
        for (int i = 0; i < globalPipeChildren.getLength(); i++) {
            if (!globalPipeChildren.item(i).getNodeName().contains("#")) {
                Node child = globalPipeChildren.item(i);

                if (child.getNodeName().equals("serialPipes")) {
                    // Pipe is serialPipes
                    if (configuredPipe instanceof SerialPipes) {
                        ((SerialPipes) configuredPipe).add(pipesFromSerial(child));
                    } else {
                        ((ParallelPipes) configuredPipe).add(pipesFromSerial(child));
                    }
                } else if (child.getNodeName().equals("parallelPipes")) {
                    // Pipe is parallelPipes
                    if (configuredPipe instanceof SerialPipes) {
                        ((SerialPipes) configuredPipe).add(pipesFromParallel(child));
                    } else {
                        ((ParallelPipes) configuredPipe).add(pipesFromParallel(child));
                    }
                } else {
                    // Pipe is a type of processing pipe
                    try {
                        if (configuredPipe instanceof SerialPipes) {
                            ((SerialPipes) configuredPipe).add(pipes.get(child.getTextContent().trim()));
                        } else {
                            ((ParallelPipes) configuredPipe).add(pipes.get(child.getTextContent().trim()));
                        }
                    } catch (NullPointerException e) {
                        logger.error("[PIPE CONFIGURATION] " + child.getTextContent().trim() +
                                " does not exist or is not loaded.");
                        System.exit(-1);
                    }
                }
            }
        }

        return configuredPipe;
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
                    serialPipes.add(pipes.get(getNameFromPipe(child)));
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
                    parallelPipes.add(pipes.get(getNameFromPipe(child)));
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

        logger.error("[GET NAME FROM PIPE] Could not get name from pipe.");
        System.exit(-1);
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
            logger.error("[PROPERTY GET] The requested property '" + k + "' does not exists.");
            System.exit(-1);
        }

        return props.get(k);
    }
}
