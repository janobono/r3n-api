package sk.r3n.example.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "EXPENSE_CATEGORY")
public class ExpenseCategory implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "NAME")
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @ColumnId(name = "NAME_SCDF")
    protected String nameScdf;

    public String getNameScdf() {
        return nameScdf;
    }

    public void setNameScdf(String nameScdf){
        this.nameScdf = nameScdf;
    }

    @ColumnId(name = "NOTE")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

}
