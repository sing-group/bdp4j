package org.bdp4j.sample.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TeePipe;
import org.bdp4j.types.Instance;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * A pipe able to measure the length of a string and create the corresponding
 * property
 *
 * @author Yeray Lage
 */
@TeePipe
public class GenerateOutputPipe extends Pipe {
    /**
     * The name of the property to store the length of a string
     */
    public static final String DEFAULT_FILE = "output.csv";
    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(GenerateOutputPipe.class);
    /**
     * The property name
     */
    String outFile = null;

    /**
     * Marks if the file has been opened
     */
    boolean fileOpened = false;

    /**
     * The output printWriter
     */
    PrintWriter out;

    /**
     * Default consturctor
     */
    public GenerateOutputPipe() {
        /**
         * Invoke the constructor with the default value
         */
        this(DEFAULT_FILE);
    }

    /**
     * Constructor customizing the property name
     */
    public GenerateOutputPipe(String outFile) {
        /* Must declare here the dependencies */
        /* alwaysBefore notAfter */
        super(new Class<?>[0], new Class<?>[0]);

        this.outFile = outFile;
    }

    /**
     * The imput type of Instance.getData
     */
    @Override
    public Class<?> getInputType() {
        return String.class;
    }

    /**
     * The output type of Instance.getData
     */
    @Override
    public Class<?> getOutputType() {
        return String.class;
    }

    /**
     * Pipe the instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        if (!fileOpened) {
            try {
                out = new PrintWriter(outFile);
                out.append("id;content;");
                for (String i : carrier.getPropertyList()) {
                    out.append(i + ";");
                }
                out.append("target\n");
            } catch (FileNotFoundException e) {
                logger.fatal(e);
                e.printStackTrace();
                System.exit(0);
            }
            fileOpened = true;
        }

        out.append(carrier.getName() + ";" + carrier.getData() + ";");
        for (Object i : carrier.getValueList()) {
            out.append(i.toString() + ";");
        }
        out.append(carrier.getTarget().toString() + "\n");

        if (isLast() && fileOpened) {
            out.close();
        }
        return carrier;
    }
}