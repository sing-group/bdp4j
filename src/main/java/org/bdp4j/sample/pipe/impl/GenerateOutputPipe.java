package org.bdp4j.sample.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.TeePipe;
import org.bdp4j.types.Instance;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * A pipe able to store the instances
 *
 * @author Yeray Lage
 */
@TeePipe
public class GenerateOutputPipe extends Pipe {
    /**
     * The default file to store CSV contents
     */
    public static final String DEFAULT_FILE = "output.csv";
    
    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(GenerateOutputPipe.class);

    /**
     * The output file
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
     * Constructor customizing the output file
     * @param outFile The file where the CSV conteng is generated
     */
    public GenerateOutputPipe(String outFile) {
        /* Must declare here the dependencies */
        /* alwaysBefore notAfter */
        super(new Class<?>[0], new Class<?>[0]);

        this.outFile = outFile;
    }

    /**
     * Setter for outFile (the filepath to store the CSV representation of Instances)
     * @param outFile the filepath to store the CSV representation of Instances
     */
    @PipeParameter(name="outFile", description="The file to store the CSV representation of instances", defaultValue=DEFAULT_FILE)
    public void setOutFile(String outFile){
        this.outFile=outFile;
    }

    /**
     * Getter for outFile (the filepath to store the CSV representation of Instances)
     * @return Return the filepath to store the CSV representation of Instances
     */
    public String getOutFile(){
        return this.outFile;
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
     * @param carrier The instance to pipe
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