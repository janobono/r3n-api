package sk.r3n.example.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "bill_activity")
public class BillActivity implements Serializable {

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

    @ColumnId(name = "account_activity_fk")
    protected Long accountActivityFk;

    public Long getAccountActivityFk() {
        return accountActivityFk;
    }

    public void setAccountActivityFk(Long accountActivityFk){
        this.accountActivityFk = accountActivityFk;
    }

}
