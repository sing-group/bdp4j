package org.bdp4j.sample.pipe.impl;

import org.bdp4j.pipe.Pipe;
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
     */
    public MeasureLengthPipe(String propName) {
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
        carrier.setProperty(propName, ((String) (carrier.getData())).length());
        return carrier;
    }
}