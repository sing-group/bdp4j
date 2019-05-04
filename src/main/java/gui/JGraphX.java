package gui;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.SerialPipes;
import org.bdp4j.util.PipeInfo;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

public class JGraphX extends JFrame {

    private static final long serialVersionUID = -2764911804288120883L;
    private static final Logger logger = LogManager.getLogger(JGraphX.class);
    private static int height = 1080;
    private final mxGraph graph;
    private Object parent;
    private int pipeY = height / 2;

    private HashMap<String, PipeInfo> pipes;

    private boolean start = false;
    private boolean xml = false;

    public JGraphX(HashMap<String, PipeInfo> pipes) {
        super("BDP4J");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1920, height);
        this.setVisible(true);

        this.pipes = pipes;

        graph = new mxGraph();

        graph.setEdgeLabelsMovable(false);
        graph.setCellsLocked(true);
        graph.setCellsDisconnectable(false);

        parent = graph.getDefaultParent();
    }

    public AbstractPipe start() {
        logger.info("[GUI] GUI started.");

        SerialPipes serialPipes = new SerialPipes();

        graph.getModel().beginUpdate();

        try {
            // Start execution button
            graph.insertVertex(parent, null, "Start", 10, 20, 80, 30);

            // Generate xml button
            graph.insertVertex(parent, null, "Generate xml", 10, 60, 80, 30);

            // Pipes buttons
            int y = 100;
            for (String p : pipes.keySet()) graph.insertVertex(parent, null, p, 10, y += 50, 200, 30);

        } finally {
            graph.getModel().endUpdate();
        }

        final mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

        graphComponent.getGraphControl().addMouseListener(
                new MouseAdapter() {
                    Object last = null;
                    Class lastType;
                    private int lastPipeX = 150;

                    public void mouseClicked(MouseEvent e) {
                        Object cell = graphComponent.getCellAt(e.getX(), e.getY());

                        if (cell != null) {
                            if (graph.getLabel(cell).equals("Start")) {
                                logger.info("[GUI] Sending pipeline...");
                                start = true;
                            } else if (graph.getLabel(cell).equals("Generate xml")) {
                                logger.info("[GUI] Generating XML...");
                                xml = true;

                            } else if (e.getX() < 205) {
                                AbstractPipe newPipe = null;
                                boolean error = false;

                                try {
                                    newPipe = (AbstractPipe) pipes.get(graph.getLabel(cell)).
                                            getPipeClass().newInstance();
                                } catch (InstantiationException | IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }

                                if (last == null) {
                                    try {
                                        Class inputType = ((AbstractPipe) pipes.get(graph.getLabel(cell)).
                                                getPipeClass().newInstance()).getInputType();

                                        if (inputType != File.class) {
                                            logger.warn("[GUI] First pipe input type must be File and was " +
                                                    inputType.getName());
                                            error = true;
                                        }
                                    } catch (InstantiationException | IllegalAccessException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    if (lastType != newPipe.getInputType()) {
                                        logger.warn("[GUI] " + lastType.getName() + " and " +
                                                newPipe.getInputType().getName() + " are not compatible.");

                                        error = true;
                                    }
                                }

                                if (!error) {
                                    serialPipes.add(newPipe);

                                    Object added = graph.insertVertex(parent, null, graph.getLabel(cell), lastPipeX += 250, pipeY - 15, 200, 30);

                                    if (last != null) {
                                        graph.insertEdge(parent, null, null, last, added);
                                    }

                                    last = added;
                                    lastType = newPipe.getOutputType();

                                    logger.info("[GUI] " + graph.getLabel(cell) + " successfully added.");
                                }
                            }
                        }
                    }
                }
        );

        // Waiting until gui work is done for resume execution.
        while (!start && !xml) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Hide the gui
        this.setVisible(false);

        // If xml generation option is selected via gui.
        if (xml) generateXml(serialPipes);

        return serialPipes;
    }

    private void generateXml(SerialPipes pipesList) {
        String xmlFilePath = "./generated/configuration.xml";

        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();

            // root element
            Element root = document.createElement("configuration");
            document.appendChild(root);

            // General element
            Element general = document.createElement("general");
            root.appendChild(general);

            // General sub-elements
            Element samplesFolder = document.createElement("samplesFolder");
            samplesFolder.appendChild(document.createTextNode("./samples"));
            Element pluginsFolder = document.createElement("pluginsFolder");
            pluginsFolder.appendChild(document.createTextNode("./plugins"));
            Element outputDir = document.createElement("outputDir");
            outputDir.appendChild(document.createTextNode("./output"));
            Element tempDir = document.createElement("tempDir");
            tempDir.appendChild(document.createTextNode("./temp"));

            general.appendChild(samplesFolder);
            general.appendChild(pluginsFolder);
            general.appendChild(outputDir);
            general.appendChild(tempDir);

            // Pipeline element
            Element pipeline = document.createElement("pipeline");
            root.appendChild(pipeline);

            // Pipeline attributes
            Attr resumable = document.createAttribute("resumable");
            resumable.setValue("yes");
            Attr debug = document.createAttribute("debug");
            debug.setValue("yes");
            pipeline.setAttributeNode(resumable);
            pipeline.setAttributeNode(debug);

            // Pipeline global serial pipe
            Element serialPipes = document.createElement("serialPipes");
            pipeline.appendChild(serialPipes);

            // Pipes in serialPipes
            for (AbstractPipe p : pipesList.getPipes()) {
                Element pipe = document.createElement("pipe");
                Element name = document.createElement("name");
                name.appendChild(document.createTextNode(p.getClass().getSimpleName()));
                pipe.appendChild(name);
                serialPipes.appendChild(pipe);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);

            logger.info("[GUI] XML File successfully created.");
            System.exit(0);
        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }
}
