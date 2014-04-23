package sk.r3n.jdbc.query;

import java.io.Serializable;

/**
 *
 * @author jan
 */
public class BaseQueryTable implements QueryTable, Serializable {

    private String name;

    private String alias;

    public BaseQueryTable(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    @Override
    public int hashCode() {
        return nameWithAlias().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof BaseQueryTable) {
            BaseQueryTable baseQueryTableObj = (BaseQueryTable) obj;
            result = baseQueryTableObj.name.equals(name) && baseQueryTableObj.alias.equals(alias);
        }
        return result;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String alias() {
        return alias;
    }

    @Override
    public String nameWithAlias() {
        StringBuilder sb = new StringBuilder();
        sb.append(name());
        sb.append(" ");
        sb.append(alias());
        return sb.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "BaseQueryTable{" + "name=" + name + ", alias=" + alias + '}';
    }

}
