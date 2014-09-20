package sk.r3n.example.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "BILL_ACTIVITY")
public class BillActivity implements Serializable {

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

    @ColumnId(name = "ACCOUNT_ACTIVITY_FK")
    protected Long accountActivityFk;

    public Long getAccountActivityFk() {
        return accountActivityFk;
    }

    public void setAccountActivityFk(Long accountActivityFk){
        this.accountActivityFk = accountActivityFk;
    }

}
