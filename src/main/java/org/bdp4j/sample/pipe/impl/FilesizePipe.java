package org.bdp4j.sample.pipe.impl;

import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
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
    public static final String DEFAULT_FILESIZE_PROP = "filesize";

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

    /**
     * Creates the pipe specifiying a property to store the size of the file containing the text
     * @param propName The name of the property to store the size of the property.
     */
    public FilesizePipe(String propName) {
        /* Must declare here the dependencies */
        /*     alwaysBefore     notAfter */
        super(new Class<?>[0], new Class<?>[0]);
        this.propName = propName;
    }

    /**
     * Setter for propName (the name of the property to store the filesize)
     * @param propName The name of the property to store the filesize
     */
    @PipeParameter(name="propName", description="The name of the property to store the filesize", defaultValue=DEFAULT_FILESIZE_PROP)
    public void setPropName(String propName){
        this.propName=propName;
    }

    /**
     * Getter for propName (the name of the property to store the filesize)
     * @return Return the name of the property to store the filesize
     */
    public String getPropName(){
        return this.getPropName();
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