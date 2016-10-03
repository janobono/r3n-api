package sk.r3n.sql;

import java.io.Serializable;

public class JoinCriterion implements Serializable {

    private Join join;

    private Table table;

    private final CriteriaManager cm;

    public JoinCriterion(Join join, Table table) {
        this.join = join;
        this.table = table;
        cm = new CriteriaManager();
    }

    public Join getJoin() {
        return join;
    }

    public void setJoin(Join join) {
        this.join = join;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public CriteriaManager getCriteriaManager() {
        return cm;
    }
}
