package sk.r3n.example.test.ora;

import java.io.Serializable;
import sk.r3n.dto.ColumnId;

public class TJoinSO implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    @ColumnId(name = "T_BASE_TYPES_FK")
    protected Long tBaseTypesFk;

    @ColumnId(name = "T_JOIN_STRING")
    protected String tJoinString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTBaseTypesFk() {
        return tBaseTypesFk;
    }

    public void setTBaseTypesFk(Long tBaseTypesFk) {
        this.tBaseTypesFk = tBaseTypesFk;
    }

    public String getTJoinString() {
        return tJoinString;
    }

    public void setTJoinString(String tJoinString) {
        this.tJoinString = tJoinString;
    }

    @Override
    public String toString() {
        return "TJoinSO{" + "id=" + id + ", tBaseTypesFk=" + tBaseTypesFk + ", tJoinString=" + tJoinString + '}';
    }

}
