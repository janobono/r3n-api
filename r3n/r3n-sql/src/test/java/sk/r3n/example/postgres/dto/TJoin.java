package sk.r3n.example.postgres.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "T_JOIN")
public class TJoin implements Serializable {

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
        return "TJoin{" + "id=" + id + ", tBaseTypesFk=" + tBaseTypesFk + ", tJoinString=" + tJoinString + '}';
    }

}
