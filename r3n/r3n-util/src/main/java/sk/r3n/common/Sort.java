package sk.r3n.common;

import java.io.Serializable;

public class Sort implements Serializable {

    private String attribute;

    private Boolean descending;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Boolean isDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    @Override
    public String toString() {
        return "Sort{" + "attribute=" + attribute + ", descending=" + descending + '}';
    }
}
