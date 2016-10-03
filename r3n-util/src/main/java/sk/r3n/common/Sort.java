package sk.r3n.common;

import java.io.Serializable;

public class Sort implements Serializable {

    public enum Order {

        ASC, DESC
    }

    private String attribute;

    private Order order;

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Sort{" + "attribute=" + attribute + ", order=" + order + '}';
    }
}
