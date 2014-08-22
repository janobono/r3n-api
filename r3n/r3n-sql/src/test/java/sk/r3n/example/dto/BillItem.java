package sk.r3n.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "bill_item")
public class BillItem implements Serializable {

    @ColumnId(name = "id")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "bill_fk")
    protected Long billFk;

    public Long getBillFk() {
        return billFk;
    }

    public void setBillFk(Long billFk){
        this.billFk = billFk;
    }

    @ColumnId(name = "note")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

    @ColumnId(name = "value")
    protected BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value){
        this.value = value;
    }

}
