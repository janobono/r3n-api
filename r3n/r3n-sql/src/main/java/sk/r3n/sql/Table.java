package sk.r3n.sql;

import java.io.Serializable;

public class Table implements Serializable {

    private String name;

    private String alias;

    public Table(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    @Override
    public int hashCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" ").append(getAlias());
        return sb.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Table) {
            result = hashCode() == ((Table) obj).hashCode();
        }
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "Table{" + "name=" + name + ", alias=" + alias + '}';
    }

}
