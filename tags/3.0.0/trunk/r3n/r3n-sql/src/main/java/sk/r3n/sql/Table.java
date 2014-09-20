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
        return nameWithAlias().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Table) {
            Table queryTableObj = (Table) obj;
            result = nameWithAlias().equals(queryTableObj.nameWithAlias());
        }
        return result;
    }

    public String nameWithAlias() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(" ");
        sb.append(alias);
        return sb.toString().toLowerCase();
    }

    @Override
    public String toString() {
        return nameWithAlias();
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

}
