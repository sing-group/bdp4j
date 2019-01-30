package org.bdp4j.util;

public class BooleanBean {
    private boolean value;

    public BooleanBean(boolean value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public boolean getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(boolean value) {
        this.value = value;
    }

    public void Or(boolean value) {
        this.value = this.value || value;
    }

    public void And(boolean value) {
        this.value = this.value && value;
    }
}