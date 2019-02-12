package org.bdp4j.util;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Pipe class with name and params.
 *
 * @author Yeray Lage
 */
public class PipeInfo {
    private String pipeName;
    private Class pipeClass;
    private HashMap<String, PipeParameter> pipeParams;

    PipeInfo(String pipeName, Class<?> pipeClass) {
        this.pipeName = pipeName;
        this.pipeClass = pipeClass;
        this.pipeParams = new HashMap<>();

        // Create an example to identify methods which have ParameterPipe annotations.
        Method[] methods = pipeClass.getMethods();

        org.bdp4j.pipe.PipeParameter parameterPipe;
        for (Method method : methods) {
            // Obtain the methods that contains any ParameterPipe annotation
            parameterPipe = method.getAnnotation(org.bdp4j.pipe.PipeParameter.class);

            if (parameterPipe != null) {
                String parameterName = parameterPipe.name();
                String parameterDescription = parameterPipe.description();
                String defaultValue = parameterPipe.defaultValue();

                // Obtains the parameter types for a method
                Class<?>[] types = method.getParameterTypes();

                pipeParams.put(parameterName, new PipeParameter(parameterName, parameterDescription, defaultValue, types));
            }
        }
    }

    public String getPipeName() {
        return pipeName;
    }

    public void setPipeName(String pipeName) {
        this.pipeName = pipeName;
    }

    public Class getPipeClass() {
        return pipeClass;
    }

    public void setPipeClass(Class pipeClass) {
        this.pipeClass = pipeClass;
    }

    public HashMap<String, PipeParameter> getPipeParams() {
        return pipeParams;
    }

    public void setPipeParams(HashMap<String, PipeParameter> pipeParams) {
        this.pipeParams = pipeParams;
    }

    public void setPipeParam(String pipeParameterName, String parameter, String value) {
        if (parameter.equals("description")) {
            pipeParams.get(pipeParameterName).setDescription(value);
        } else if (parameter.equals("defaultValue")) {
            pipeParams.get(pipeParameterName).setDefaultValue(value);
        }
    }
}