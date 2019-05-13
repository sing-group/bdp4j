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

    private int pipeLineY;

    private HashMap<String, PipeInfo> pipes;
    private HashMap<String, String> generalConfiguration;

    private String globalStyle = "fontSize=20;fontColor=black;verticalAlign=middle;editable=0;bendable=0;" +
            "movable=0;resizable=0;foldable=0;deletable=0;rotable=0;cloneable=0;verticalLabelPosition=middle;" +
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

            String fieldStyle = "editable=1;fillColor=#e3f2fd;textOpacity=70;";

            Object options = graph.insertVertex(parent, null, null, 600, 150, 440, 280, globalStyle + "opacity=30;rounded=0;");

            // Samples folder
            Object samples1 = graph.insertVertex(options, null, "Samples folder", 20, 20, 150, 40, globalStyle);
            Object samples2 = graph.insertVertex(options, null, "./samples", 220, 20, 200, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, samples1, samples2);

            // Plugins folder
            Object plugins1 = graph.insertVertex(options, null, "Plugins folder", 20, 70, 150, 40, globalStyle);
            Object plugins2 = graph.insertVertex(options, null, "./plugins", 220, 70, 200, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, plugins1, plugins2);

            // Output dir
            Object outputDir1 = graph.insertVertex(options, null, "Output dir", 20, 120, 150, 40, globalStyle);
            Object outputDir2 = graph.insertVertex(options, null, "./output", 220, 120, 200, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, outputDir1, outputDir2);

            // Temp dir
            Object tempDir1 = graph.insertVertex(options, null, "Temp dir", 20, 170, 150, 40, globalStyle);
            Object tempDir2 = graph.insertVertex(options, null, "./temp", 220, 170, 200, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, tempDir1, tempDir2);

            // Debug
            Object debug1 = graph.insertVertex(options, null, "Debug", 20, 220, 150, 40, globalStyle);
            Object debug2 = graph.insertVertex(options, null, "yes", 220, 220, 200, 40, globalStyle + fieldStyle);
            graph.insertEdge(options, null, null, debug1, debug2);

            // Pipes buttons
            int y = 100;
            for (String p : pipes.keySet()) {
                graph.insertVertex(parent, null, p, 20, y += 50, 450, 40, globalStyle + "fillColor=#80cbc4");
            }
            pipeLineY = y;
        } finally {
            graph.getModel().endUpdate();
        }

        final mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);

        graphComponent.getGraphControl().addMouseListener(
                new MouseAdapter() {
                    Object last = null;
                    Class lastType;
                    private int lastPipeX = 20;

                    public void mouseClicked(MouseEvent e) {
                        Object cell = graphComponent.getCellAt(e.getX(), e.getY());

                        if (cell != null) {
                            if (graph.getLabel(cell).equals("Start")) {
                                logger.info("[GUI] Sending pipeline...");
                                start = true;
                            } else if (graph.getLabel(cell).equals("Generate xml")) {
                                logger.info("[GUI] Generating XML...");
                                xml = true;

                            } else if (e.getX() < 460 && e.getY() < pipeLineY + 40) {
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

                                    if (lastPipeX + 450 >= 1900) {
                                        pipeLineY += 100;
                                        lastPipeX = 20;
                                    }

                                    Object added = graph.insertVertex(parent, null, graph.getLabel(cell), lastPipeX, pipeLineY + 100, 450, 40, globalStyle + "fillColor=#b0bec5");
                                    lastPipeX += 550;

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
        String[] configurations = {"samplesFolder", "pluginsFolder", "outputDir", "tempDir", "debugMode"};
        int y = 120;
        for (String key : configurations) {
            Object cell = graphComponent.getCellAt(820, y += 50);
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
            resumable.setValue("yes");
            Attr debug = document.createAttribute("debug");
            debug.setValue(generalConfiguration.get("debugMode"));
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
        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }
}
