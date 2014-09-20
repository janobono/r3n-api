package sk.r3n.example.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "H_PROPERTIES")
public class HProperties implements Serializable {

    @ColumnId(name = "ID")
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }

    @ColumnId(name = "VALUE")
    protected String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }

}
