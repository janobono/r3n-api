package sk.r3n.example.dto;

import java.io.Serializable;
import java.util.Date;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "BILL")
public class Bill implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "NOTE")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    @ColumnId(name = "NOTE_SCDF")
    protected String noteScdf;

    public String getNoteScdf() {
        return noteScdf;
    }

    public void setNoteScdf(String noteScdf){
        this.noteScdf = noteScdf;
    }

    @ColumnId(name = "BILL_DATE")
    protected Date billDate;

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate){
        this.billDate = billDate;
    }

    @ColumnId(name = "BILL_TIME")
    protected Date billTime;

    public Date getBillTime() {
        return billTime;
    }

    public void setBillTime(Date billTime){
        this.billTime = billTime;
    }

}
