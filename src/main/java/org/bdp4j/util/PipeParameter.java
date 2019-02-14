package org.bdp4j.util;

public class PipeParameter {
    private String paramName;
    private String description;
    private String defaultValue;
    private String value;
    private Class<?>[] types;

    PipeParameter(String paramName, String description, String defaultValue, Class<?>[] types) {
        this.paramName = paramName;
        this.description = description;
        this.defaultValue = defaultValue;
        this.types = types;
        this.value = null;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        // Just changes it if is different to default
        if (!defaultValue.equals(value))
            this.value = value;
    }
}
