package sk.r3n.jdbc.query;

import java.io.Serializable;

/**
 *
 * @author jan
 */
public class BaseQueryAttribute implements QueryAttribute, Serializable {

    private String name;

    private QueryTable table;

    private DataType dataType;

    public BaseQueryAttribute(String name, QueryTable table, DataType dataType) {
        this.name = name;
        this.table = table;
        this.dataType = dataType;
    }

    @Override
    public int hashCode() {
        return nameWithAlias().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof BaseQueryAttribute) {
            BaseQueryAttribute baseQueryAttributeObj = (BaseQueryAttribute) obj;
            result = baseQueryAttributeObj.name.equals(name) && baseQueryAttributeObj.table.equals(table);
        }
        return result;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public QueryTable table() {
        return table;
    }

    @Override
    public String nameWithAlias() {
        StringBuilder sb = new StringBuilder();
        sb.append(table().alias());
        sb.append(".");
        sb.append(name());
        return sb.toString();
    }

    @Override
    public DataType dataType() {
        return dataType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTable(QueryTable table) {
        this.table = table;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return "BaseQueryAttribute{" + "name=" + name + ", table=" + table + ", dataType=" + dataType + '}';
    }

}
