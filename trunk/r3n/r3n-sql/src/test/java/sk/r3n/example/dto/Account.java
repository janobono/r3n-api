package sk.r3n.example.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "account")
public class Account implements Serializable {

    @ColumnId(name = "id")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ColumnId(name = "type")
    protected Short type;

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    @ColumnId(name = "name")
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ColumnId(name = "name_scdf")
    protected String nameScdf;

    public String getNameScdf() {
        return nameScdf;
    }

    public void setNameScdf(String nameScdf) {
        this.nameScdf = nameScdf;
    }

    @ColumnId(name = "note")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Account{" + "id=" + id + ", type=" + type + ", name=" + name + ", nameScdf=" + nameScdf + ", note=" + note + '}';
    }

}
