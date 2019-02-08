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

public class Configurator {
    private static final Logger logger = LogManager.getLogger(Configurator.class);
    private static Configurator ourInstance = new Configurator();
    private HashMap<String, String> props;
    private Document document;

    private Configurator() {
        props = new HashMap<>();

        try {
            File file = new File("./config/configuration.xml");
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);

            document.getDocumentElement().normalize();
        } catch (Exception e) {
            logger.error("[CONFIGURATION] Error loading.");
        }
    }

    public static Configurator getInstance() {
        return ourInstance;
    }

    public void configureApp() {
        NodeList generalChildren = document.getElementsByTagName("general").item(0).getChildNodes();

        for (int i = 0; i < generalChildren.getLength(); i++) {
            Node child = generalChildren.item(i);

            if (!generalChildren.item(i).getNodeName().contains("#")) {
                logger.info("[PROPERTIES LOAD] " + child.getNodeName() + " -> " + child.getTextContent().trim() + ".");
                props.put(child.getNodeName(), child.getTextContent().trim());
            }
        }
    }

    public Pipe configurePipe(HashMap<String, Pipe> pipes) {
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
        }

        // Global pipe children
        NodeList globalPipeChildren = globalPipe.getChildNodes();

        // Now we iterate over the global pipe children
        for (int i = 0; i < globalPipeChildren.getLength(); i++) {
            if (!globalPipeChildren.item(i).getNodeName().contains("#")) {
                Node child = globalPipeChildren.item(i);

                try {
                    if (globalPipe.getNodeName().equals("serialPipes")) {
                        ((SerialPipes) configuredPipe).add(pipes.get(child.getTextContent().trim()));
                    } else if (globalPipe.getNodeName().equals("parallelPipes")) {
                        ((ParallelPipes) configuredPipe).add(pipes.get(child.getTextContent().trim()));
                    }
                } catch (NullPointerException e) {
                    logger.error("[PIPE CONFIGURATION] " + child.getTextContent().trim() + " does not exist or is not loaded.");
                    System.exit(-1);
                }
            }
        }

        return configuredPipe;
    }

    public String getProp(String k) {
        // Check if the property exists.
        if (props.get(k) == null) {
            logger.error("[PROPERTY GET] The requested property '" + k + "' does not exists.");
            System.exit(-1);
        }

        return props.get(k);
    }
}
