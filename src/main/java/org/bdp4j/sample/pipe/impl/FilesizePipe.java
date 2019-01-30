package org.bdp4j.sample.pipe.impl;

import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

import java.io.File;

/**
 * A pipe able to compute the size of a file
 *
 * @author Yeray Lage
 */
@PropertyComputingPipe
public class FilesizePipe extends Pipe {
    /**
     * The name of the deafult propety to store the filesize
     */
    public static String DEFAULT_FILESIZE_PROP = "filesize";

    /**
     * The property Name to store the filesize
     */
    String propName = null;

    /**
     * Default consturctor
     */
    public FilesizePipe() {
        this(DEFAULT_FILESIZE_PROP);
    }

    public FilesizePipe(String propName) {
        /* Must declare here the dependencies */
        /*     alwaysBefore     notAfter */
        super(new Class<?>[0], new Class<?>[0]);
        this.propName = propName;
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
        carrier.setProperty(propName, ((File) (carrier.getData())).length());

        return carrier;
    }

}