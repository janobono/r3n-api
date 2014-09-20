package sk.r3n.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "BILL_ITEM")
public class BillItem implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "BILL_FK")
    protected Long billFk;

    public Long getBillFk() {
        return billFk;
    }

    public void setBillFk(Long billFk){
        this.billFk = billFk;
    }

    @ColumnId(name = "NOTE")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    @ColumnId(name = "VALUE")
    protected BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value){
        this.value = value;
    }

}
