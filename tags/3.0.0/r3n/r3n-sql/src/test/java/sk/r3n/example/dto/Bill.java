package sk.r3n.example.dto;

import java.io.Serializable;
import java.util.Date;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "bill")
public class Bill implements Serializable {

    @ColumnId(name = "id")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "note")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    @ColumnId(name = "note_scdf")
    protected String noteScdf;

    public String getNoteScdf() {
        return noteScdf;
    }

    public void setNoteScdf(String noteScdf){
        this.noteScdf = noteScdf;
    }

    @ColumnId(name = "bill_date")
    protected Date billDate;

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate){
        this.billDate = billDate;
    }

    @ColumnId(name = "bill_time")
    protected Date billTime;

    public Date getBillTime() {
        return billTime;
    }

    public void setBillTime(Date billTime){
        this.billTime = billTime;
    }

}
