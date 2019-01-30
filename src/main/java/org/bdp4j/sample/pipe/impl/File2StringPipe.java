package org.bdp4j.sample.pipe.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.TransformationPipe;
import org.bdp4j.types.Instance;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A pipe able to load the text from a file (i.e. a file conteining the text of
 * an SMS message)
 *
 * @author Yeray Lage
 */
@TransformationPipe
public class File2StringPipe extends Pipe {
    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(File2StringPipe.class);

    /**
     * Default consturctor
     */
    public File2StringPipe() {
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
        return String.class;
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
        else carrier.setData(loadedStr.substring(0, loadedStr.lastIndexOf(","))); //And set the data otherwise

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