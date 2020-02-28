/*-
 * #%L
 * BDP4J
 * %%
 * Copyright (C) 2018 - 2020 SING Group (University of Vigo)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.pipe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bdp4j.types.Instance;
import org.bdp4j.util.RegularExpressionEvaluator;

/**
 *
 * @author María Novo
 */
public class CombinePropertiesPipe extends AbstractPipe {

    /**
     * For logging purposes
     */
    private static final Logger logger = LogManager.getLogger(CombinePropertiesPipe.class);

    /**
     * The default name of the property to store the new combine property
     */
    public static final String DEFAULT_COMBINE_PROPERTY = "combine";

    /**
     * The property to store the new combine property
     */
    private String combineProp = DEFAULT_COMBINE_PROPERTY;

    /**
     * The property to store the regular expression used to combine columns
     */
    private String regex;

    /**
     * The property to store the type of the returned result from evaluate columns
     */
    private Class expressionType;

    /**
     * The property to store the type of the name of columns to combine
     */
    private String[] parameterNames;

    /**
     * The property to store the type of the type of columns to combine
     */
    private Class[] parameterTypes;

    /**
     * Default constructor. Build a CombineColumnsFromStringBufferPipe that stores
     * the result of combined columns in the default property ("combine")
     */
    public CombinePropertiesPipe(String regex, Class expressionType, String[] parameterNames, Class[] parameterTypes) {
        this(DEFAULT_COMBINE_PROPERTY, regex, expressionType, parameterNames, parameterTypes);
    }

    /**
     * Build a MeasureLengthFromStringBufferPipe that stores the result of combined
     * columns in the property indicated by combineProp parameter
     *
     * @param combineProp the name of the property to store the text length
     */
    public CombinePropertiesPipe(String combineProp, String regex, Class expressionType, String[] parameterNames,
            Class[] parameterTypes) {
        super(new Class<?>[0], new Class<?>[0]);

        this.combineProp = combineProp;
        this.regex = regex;
        this.expressionType = expressionType;
        this.parameterNames = parameterNames;
        this.parameterTypes = parameterTypes;
    }

    /**
     * Return the input type included the data attribute of an Instance
     *
     * @return the input type for the data attribute of the Instance processed
     */
    @Override
    public Class<?> getInputType() {
        return StringBuffer.class;
    }

    /**
     * Indicates the datatype expected in the data attribute of an Instance after
     * processing
     *
     * @return the datatype expected in the data attribute of an Instance after
     *         processing
     */
    @Override
    public Class<?> getOutputType() {
        return StringBuffer.class;
    }

    /**
     * Establish the name of the property to store the result of combined columns
     *
     * @param combineProp the name of the property to store the result of combined
     *                    columns text
     */
    @PipeParameter(name = "combinePropname", description = "Indicates the property name to store the result of combined columns", defaultValue = DEFAULT_COMBINE_PROPERTY)
    public void setCombineProp(String combineProp) {
        this.combineProp = combineProp;
    }

    /**
     * Returns the name of the property to store the result of combined columns
     *
     * @return the name of the property to store the result of combined columns
     */
    public String getCombineProp() {
        return this.combineProp;
    }

    /**
     * Process an Instance. This method takes an input Instance, calculates the the
     * result of indicated by regex combined columns, and returns it. This is the
     * method by which all pipes are eventually run.
     *
     * @param carrier Instance to be processed.
     * @return Instance processed
     * @throws Exception
     */
    @Override
    public Instance pipe(Instance carrier) {

        RegularExpressionEvaluator ree = new RegularExpressionEvaluator();
        Object[] parameterValues = new Object[parameterNames.length];

        for (int i = 0; i < parameterNames.length; i++) {
            if (carrier.getProperty(parameterNames[i]) instanceof Integer) {
                parameterValues[i] = (Integer) carrier.getProperty(parameterNames[i]);
            } else if (carrier.getProperty(parameterNames[i]) instanceof Double) {
                parameterValues[i] = (Double) carrier.getProperty(parameterNames[i]);
            } else {
                parameterValues[i] = carrier.getProperty(parameterNames[i]).toString();
            }
        }
        try {
            Object result = ree.evaluateExpression(this.regex, this.expressionType, this.parameterNames,
                    this.parameterTypes, parameterValues);
            if (result instanceof Integer) {
                carrier.setProperty(combineProp, (Integer) result);
            } else if (result instanceof Double) {
                carrier.setProperty(combineProp, (Double) result);
            } else {
                carrier.setProperty(combineProp, (String) result);
            }
        } catch (Exception ex) {
            logger.error("ERROR: " + this.getClass()  +" - pipe. " + ex.getMessage());
        }

        return carrier;
    }

}
