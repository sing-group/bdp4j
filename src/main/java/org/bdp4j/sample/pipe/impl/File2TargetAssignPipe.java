package org.bdp4j.sample.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TargetAssigningPipe;
import org.bdp4j.types.Instance;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A pipe able to find the target for the SMS representation
 *
 * @author Yeray Lage
 */
@TargetAssigningPipe
public class File2TargetAssignPipe extends Pipe {
    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(File2TargetAssignPipe.class);

    /**
     * Default consturctor
     */
    public File2TargetAssignPipe() {
        /* Must declare here the dependencies */
        /*     alwaysBefore     notAfter */
        super(new Class<?>[0], new Class<?>[0]);
    }

    /**
     * The imput type of Instance.getData
     */
    @Override
    public Class<?> getInputType() {
        return File.class;
    }

    /**
     * The output type of Instance.getData
     */
    @Override
    public Class<?> getOutputType() {
        return File.class;
    }

    /**
     * Pipe the instance
     */
    @Override
    public Instance pipe(Instance carrier) {
        //Load the contents of the file
        String loadedStr = loadFile((File) carrier.getData());

        //Invalidate the instance if data couldn't be loaded
        if (loadedStr == null) carrier.invalidate();
        else
            carrier.setTarget(loadedStr.substring(loadedStr.lastIndexOf(",") + 1).replaceAll("[\\p{Cntrl}\\p{Space}]", "")); //And set the data otherwise

        return carrier;
    }

    /**
     * Read the full contents of a file into a string
     *
     * @param file The file (java.io.File)
     * @return The string representation of the contents for the file
     */
    private String loadFile(File file) {
        byte[] encoded = null;
        try {
            encoded = Files.readAllBytes(Paths.get(file.getPath()));
        } catch (IOException e) {
            logger.error(e);
            return null;
        }
        return new String(encoded, Charset.defaultCharset());
    }

}