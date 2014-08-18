package sk.r3n.sql;

import java.io.Serializable;

public class OrderCriterion implements Serializable {

    private Column column;

    private Order order;

    public OrderCriterion(Column column, Order order) {
        this.column = column;
        this.order = order;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

}
