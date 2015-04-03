package sk.r3n.example.h2.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "T_JOIN")
public class TJoin implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "T_BASE_TYPES_FK")
    protected Long tBaseTypesFk;

    public Long getTBaseTypesFk() {
        return tBaseTypesFk;
    }

    public void setTBaseTypesFk(Long tBaseTypesFk){
        this.tBaseTypesFk = tBaseTypesFk;
    }

    @ColumnId(name = "T_JOIN_STRING")
    protected String tJoinString;

    public String getTJoinString() {
        return tJoinString;
    }

    public void setTJoinString(String tJoinString){
        this.tJoinString = tJoinString;
    }

}
