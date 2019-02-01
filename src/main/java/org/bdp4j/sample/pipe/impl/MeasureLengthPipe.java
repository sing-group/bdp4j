package org.bdp4j.sample.pipe.impl;

import org.bdp4j.pipe.Pipe;
import org.bdp4j.pipe.PipeParameter;
import org.bdp4j.pipe.PropertyComputingPipe;
import org.bdp4j.types.Instance;

/**
 * A pipe able to measure the length of a string and create the
 * corresponding property
 *
 * @author Yeray Lage
 */
@PropertyComputingPipe
public class MeasureLengthPipe extends Pipe {
    /**
     * The name of the property to store the length of a string
     */
    public static final String DEFAULT_LENGTH_PROP_NAME = "length";

    /**
     * The property name
     */
    String propName = null;

    /**
     * Default consturctor
     */
    public MeasureLengthPipe() {
        /**
         * Invoke the constructor with the default value
         */
        this(DEFAULT_LENGTH_PROP_NAME);
    }

    /**
     * Constructor customizing the property name
     * @param propName The property name to store the length
     */
    public MeasureLengthPipe(String propName) {
        /* Must declare here the dependencies */
        /*     alwaysBefore     notAfter */
        super(new Class<?>[0], new Class<?>[0]);

        this.propName = propName;
    }

    /**
     * Setter for propName (the name of the property to store the length)
     * @param propName The name of the property to store the length
     */
    @PipeParameter(name="propName", description="The name of the property to store the length of the text", defaultValue=DEFAULT_LENGTH_PROP_NAME)
    public void setPropName(String propName){
        this.propName=propName;
    }

    /**
     * Getter for propName (the name of the property to store the length)
     * @return Return the name of the property to store the length
     */
    public String getPropName(){
        return this.getPropName();
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
        carrier.setProperty(propName, ((String) (carrier.getData())).length());
        return carrier;
    }
}