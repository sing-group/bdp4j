package gui;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.AbstractPipe;
import org.bdp4j.pipe.ParallelPipes;
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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;

public class JGraphX extends JFrame {

    private static final long serialVersionUID = -2764911804288120883L;
    private static final Logger logger = LogManager.getLogger(JGraphX.class);

    private final mxGraph graph;
    private Object parent;
    private Object pipeline;
    private Object parallelButton;
    private Object serialButton;

    private int pipeLineY;

    private HashMap<String, PipeInfo> pipes;
    private HashMap<String, String> generalConfiguration;

    private String globalStyle = "fontSize=20;fontColor=black;verticalAlign=middle;editable=0;bendable=0;" +
            "movable=0;resizable=0;foldable=0;rotable=0;cloneable=0;verticalLabelPosition=middle;" +
            "labelPosition=center;rounded=1;";

    private boolean start = false;
    private boolean xml = false;

    public JGraphX(HashMap<String, PipeInfo> pipes) {
        super("BDP4J");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setMinimumSize(new Dimension(960, 540));
        this.setMaximumSize(new Dimension(1920, 1080));
        this.setPreferredSize(new Dimension(1920, 1080));
        this.setLocationRelativeTo(null);

        //Start on fullscreen
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);

        this.pipes = pipes;

        graph = new mxGraph();

        parent = graph.getDefaultParent();
        generalConfiguration = new HashMap<>();
    }

    public AbstractPipe start() {
        logger.info("[GUI] GUI started.");

        SerialPipes serialPipes = new SerialPipes();

        graph.getModel().beginUpdate();
        try {
            // Start execution button
            graph.insertVertex(parent, null, "Start", 20, 50, 150, 60, globalStyle + "fontSize=30;fillColor=#81c784");

            // Generate xml button
            graph.insertVertex(parent, null, "Generate xml", 200, 50, 270, 60, globalStyle + "fontSize=30;fillColor=#ff8a65;");

            // Pipes buttons
            int y = 100;
            for (String p : pipes.keySet()) {
                graph.insertVertex(parent, null, p, 20, y += 50, 450, 40, globalStyle + "fillColor=#80cbc4");
            }
            pipeLineY = y;

            String fieldStyle = "editable=1;fillColor=#e3f2fd;textOpacity=70;";

            Object options = graph.insertVertex(parent, null, null, 20, pipeLineY + 50, 450, 330, globalStyle + "opacity=30;rounded=0;");

            // Samples folder
            System.out.println("Is on: 40-" + (pipeLineY + 50 + 20));
            Object samples1 = graph.insertVertex(options, null, "Samples folder", 20, 20, 150, 40, globalStyle);
            Object samples2 = graph.insertVertex(options, null, "./samples", 220, 20, 210, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, samples1, samples2);

            // Plugins folder
            Object plugins1 = graph.insertVertex(options, null, "Plugins folder", 20, 70, 150, 40, globalStyle);
            Object plugins2 = graph.insertVertex(options, null, "./plugins", 220, 70, 210, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, plugins1, plugins2);

            // Output dir
            Object outputDir1 = graph.insertVertex(options, null, "Output dir", 20, 120, 150, 40, globalStyle);
            Object outputDir2 = graph.insertVertex(options, null, "./output", 220, 120, 210, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, outputDir1, outputDir2);

            // Temp dir
            Object tempDir1 = graph.insertVertex(options, null, "Temp dir", 20, 170, 150, 40, globalStyle);
            Object tempDir2 = graph.insertVertex(options, null, "./temp", 220, 170, 210, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, tempDir1, tempDir2);

            // Debug
            Object debug1 = graph.insertVertex(options, null, "Debug", 20, 220, 150, 40, globalStyle);
            Object debug2 = graph.insertVertex(options, null, "yes", 220, 220, 210, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, debug1, debug2);

            // Resumable
            Object resumable1 = graph.insertVertex(options, null, "Resumable", 20, 270, 150, 40, globalStyle);
            Object resumable2 = graph.insertVertex(options, null, "yes", 220, 270, 210, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, debug1, debug2);

            // Add serialPipes
            serialButton = graph.insertVertex(parent, null, null, 20, pipeLineY + 90 + 300, 200, 40, globalStyle + "opacity=0;deletable=0;");
            graph.insertVertex(serialButton, null, "Add SerialPipes", 0, 0, 200, 40, globalStyle + "fillColor=#aab6fe;deletable=0;");
            // Add parallelPipes
            parallelButton = graph.insertVertex(parent, null, null, 270, pipeLineY + 90 + 300, 200, 40, globalStyle + "opacity=0;deletable=0;");
            graph.insertVertex(parallelButton, null, "Add ParallelPipes", 0, 0, 200, 40, globalStyle + "fillColor=#aab6fe;deletable=0;");

            // Clear
            graph.insertVertex(parent, null, "Clear all", 20, pipeLineY + 50 + 340 + 50 + 50, 450, 40, globalStyle + "fillColor=#ffccbc;rounded=1;");

            //Pipeline
            pipeline = graph.insertVertex(parent, null, null, 500, 10, getWidth(), getHeight(), globalStyle + "opacity=0;");
        } finally {
            graph.getModel().endUpdate();
        }

        final mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

        graphComponent.getGraphControl().addMouseListener(
                new MouseAdapter() {
                    // Initial serialPipes
                    Object firstSerial = graph.insertVertex(parent, null, "SerialPipes", 520, getHeight() / 2, 270, 40, globalStyle + "fillColor=#b0bec5;");
                    Object last = firstSerial;

                    Class lastType;
                    private int lastPipeX = 350;
                    private int lastPipeY = 20;

                    private SerialPipes auxSerial = null;
                    private ParallelPipes auxParallel = null;

                    private Object auxObject = null;
                    private int auxObjectY;

                    public void mouseClicked(MouseEvent e) {
                        Object cell = graphComponent.getCellAt(e.getX(), e.getY());

                        if (cell != null) {
                            if (graph.getLabel(cell).equals("Clear all")) {
                                // Clean interface
                                graph.removeCells(graph.getChildCells(pipeline));

                                // Clean serialPipes
                                int size = serialPipes.getPipes().length;
                                for (int i = 0; i < size; i++) serialPipes.removePipe(0);

                                last = firstSerial;
                                lastPipeX = 350;
                                lastPipeY = 20;

                                graph.removeCells(graph.getChildCells(serialButton));
                                graph.removeCells(graph.getChildCells(parallelButton));
                            } else if (graph.getLabel(cell).equals("Start")) {
                                logger.info("[GUI] Sending pipeline...");
                                start = true;
                            } else if (graph.getLabel(cell).equals("Generate xml")) {
                                logger.info("[GUI] Generating XML...");
                                xml = true;

                            } else if (graph.getLabel(cell).equals("Add SerialPipes")) {
                                // Close parallelPipes
                                graph.insertVertex(serialButton, null, "Close SerialPipes", 0, 50, 200, 40, globalStyle + "fillColor=#c5cae9;");
                                auxSerial = new SerialPipes();

                                auxObject = graph.insertVertex(pipeline, null, "SerialPipes", lastPipeX, lastPipeY + 100, 450, 40, globalStyle + "fillColor=#b0bec5");
                                auxObjectY = lastPipeY + 100;
                                lastPipeY += 100;

                                graph.insertEdge(pipeline, null, null, last, auxObject);

                                last = auxObject;

                                logger.info("[GUI] SerialPipes successfully added.");
                            } else if (graph.getLabel(cell).equals("Add ParallelPipes")) {
                                // Close serialPipes
                                graph.insertVertex(parallelButton, null, "Close ParallelPipes", 0, 50, 200, 40, globalStyle + "fillColor=#c5cae9;");

                                auxParallel = new ParallelPipes();

                                auxObject = graph.insertVertex(pipeline, null, "ParallelPipes", lastPipeX, lastPipeY + 100, 450, 40, globalStyle + "fillColor=#b0bec5");
                                auxObjectY = lastPipeY + 100;
                                lastPipeY += 100;

                                graph.insertEdge(pipeline, null, null, last, auxObject);

                                last = auxObject;
                                logger.info("[GUI] ParallelPipes successfully added.");
                            } else if (graph.getLabel(cell).equals("Close SerialPipes")) {
                                graph.removeCells(graph.getChildCells(serialButton));
                                serialPipes.add(auxSerial);
                                lastPipeY = auxObjectY;

                                auxSerial = null;

                                graph.insertEdge(pipeline, null, null, last, auxObject);

                                last = auxObject;
                                logger.info("[GUI] SerialPipes successfully closed.");
                            } else if (graph.getLabel(cell).equals("Close ParallelPipes")) {
                                graph.removeCells(graph.getChildCells(parallelButton));
                                serialPipes.add(auxParallel);
                                lastType = auxParallel.getOutputType();
                                lastPipeY = auxObjectY;

                                auxParallel = null;

                                graph.insertEdge(pipeline, null, null, last, auxObject);

                                last = auxObject;
                                logger.info("[GUI] ParallelPipes successfully closed.");
                            } else if (e.getX() < 460 && e.getY() < pipeLineY + 40) {
                                // If add custom task clicked
                                AbstractPipe newPipe = null;
                                boolean error = false;

                                try {
                                    newPipe = (AbstractPipe) pipes.get(graph.getLabel(cell)).
                                            getPipeClass().newInstance();
                                } catch (InstantiationException | IllegalAccessException e1) {
                                    e1.printStackTrace();
                                }

                                if (last == firstSerial) {
                                    try {
                                        Class inputType = ((AbstractPipe) pipes.get(graph.getLabel(cell)).
                                                getPipeClass().newInstance()).getInputType();

                                        if (inputType != File.class) {
                                            JOptionPane.showMessageDialog(graphComponent, "Error: First pipe" +
                                                            " input type must be java.io.File and was " +
                                                            inputType.getName(), "Not valid first Pipe",
                                                    JOptionPane.ERROR_MESSAGE);

                                            logger.warn("[GUI] First pipe input type must be File and was " +
                                                    inputType.getName());

                                            error = true;
                                        }
                                    } catch (InstantiationException | IllegalAccessException e1) {
                                        e1.printStackTrace();
                                    }
                                } else {
                                    if (lastType != newPipe.getInputType()) {
                                        JOptionPane.showMessageDialog(graphComponent, "Error: Pipe types " +
                                                lastType.getName() + " and " + newPipe.getInputType().getName() +
                                                " are not compatible.", "Types error", JOptionPane.ERROR_MESSAGE);

                                        logger.warn("[GUI] " + lastType.getName() + " and " +
                                                newPipe.getInputType().getName() + " are not compatible.");

                                        error = true;
                                    }
                                }

                                if (!error) {
                                    Object added;

                                    if (auxParallel != null || auxSerial != null) {
                                        if (auxParallel != null) {
                                            auxParallel.add(newPipe);
                                            lastType = auxParallel.getInputType();
                                        } else {
                                            auxSerial.add(newPipe);
                                            lastType = newPipe.getOutputType();
                                        }

                                        added = graph.insertVertex(pipeline, null, graph.getLabel(cell), lastPipeX + 500, lastPipeY - 100, 450, 40, globalStyle + "fillColor=#b0bec5");
                                    } else {
                                        serialPipes.add(newPipe);
                                        added = graph.insertVertex(pipeline, null, graph.getLabel(cell), lastPipeX, lastPipeY + 100, 450, 40, globalStyle + "fillColor=#b0bec5");
                                        lastType = newPipe.getOutputType();
                                    }


                                    lastPipeY += 100;

                                    graph.insertEdge(pipeline, null, null, last, added);

                                    last = added;


                                    logger.info("[GUI] " + graph.getLabel(cell) + " successfully added.");
                                }
                            }
                        }
                    }
                }
        );

        this.pack();
        this.setVisible(true);

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

        // Save configurations
        String[] configurations = {"samplesFolder", "pluginsFolder", "outputDir", "tempDir", "debugMode", "resumable"};
        int y = pipeLineY + 20;

        System.out.println("Searched on: " + y);

        for (String key : configurations) {
            Object cell = graphComponent.getCellAt(240, y += 50);
            String value = graph.getLabel(cell);
            generalConfiguration.put(key, value);
        }

        // XML generation from gui data
        generateXml(serialPipes, generalConfiguration);

        // Just generate XML and exit
        if (xml) System.exit(0);

        return serialPipes;
    }

    private void generateXml(SerialPipes pipesList, HashMap<String, String> generalConfiguration) {
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
            samplesFolder.appendChild(document.createTextNode(generalConfiguration.get("samplesFolder")));
            Element pluginsFolder = document.createElement("pluginsFolder");
            pluginsFolder.appendChild(document.createTextNode(generalConfiguration.get("pluginsFolder")));
            Element outputDir = document.createElement("outputDir");
            outputDir.appendChild(document.createTextNode(generalConfiguration.get("outputDir")));
            Element tempDir = document.createElement("tempDir");
            tempDir.appendChild(document.createTextNode(generalConfiguration.get("tempDir")));

            general.appendChild(samplesFolder);
            general.appendChild(pluginsFolder);
            general.appendChild(outputDir);
            general.appendChild(tempDir);

            // Pipeline element
            Element pipeline = document.createElement("pipeline");
            root.appendChild(pipeline);

            // Pipeline attributes
            Attr resumable = document.createAttribute("resumable");
            resumable.setValue(generalConfiguration.get("resumable"));
            Attr debug = document.createAttribute("debug");
            debug.setValue(generalConfiguration.get("debugMode"));
            pipeline.setAttributeNode(resumable);
            pipeline.setAttributeNode(debug);

            // Pipeline global serial pipe
            Element serialPipes = document.createElement("serialPipes");
            pipeline.appendChild(serialPipes);

            // Pipes in serialPipes
            for (AbstractPipe p : pipesList.getPipes()) {
                if (p.getClass().getSimpleName().equals("SerialPipes")) {
                    // Serial Pipe
                    Element sp = document.createElement("serialPipes");
                    Element pipe = null;
                    for (AbstractPipe subP : ((SerialPipes) p).getPipes()) {
                        pipe = document.createElement("pipe");
                        Element name = document.createElement("name");
                        name.appendChild(document.createTextNode(subP.getClass().getSimpleName()));
                        pipe.appendChild(name);
                    }
                    sp.appendChild(pipe);
                    serialPipes.appendChild(sp);
                } else if (p.getClass().getSimpleName().equals("ParallelPipes")) {
                    // Parallel Pipe
                    Element pp = document.createElement("parallelPipes");
                    Element pipe = null;
                    for (AbstractPipe subP : ((ParallelPipes) p).getPipes()) {
                        pipe = document.createElement("pipe");
                        Element name = document.createElement("name");
                        name.appendChild(document.createTextNode(subP.getClass().getSimpleName()));
                        pipe.appendChild(name);
                    }
                    pp.appendChild(pipe);
                    serialPipes.appendChild(pp);
                } else {
                    // Normal Pipe
                    Element pipe = document.createElement("pipe");
                    Element name = document.createElement("name");
                    name.appendChild(document.createTextNode(p.getClass().getSimpleName()));
                    pipe.appendChild(name);
                    serialPipes.appendChild(pipe);
                }
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            transformer.transform(domSource, streamResult);

            logger.info("[GUI] XML File successfully created.");
        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }
}
