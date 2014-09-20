package sk.r3n.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "ACCOUNT_ACTIVITY")
public class AccountActivity implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "ACCOUNT_FK")
    protected Long accountFk;

    public Long getAccountFk() {
        return accountFk;
    }

    public void setAccountFk(Long accountFk){
        this.accountFk = accountFk;
    }

    @ColumnId(name = "TYPE")
    protected Short type;

    public Short getType() {
        return type;
    }

    public void setType(Short type){
        this.type = type;
    }

    @ColumnId(name = "SUB_TYPE")
    protected Short subType;

    public Short getSubType() {
        return subType;
    }

    public void setSubType(Short subType){
        this.subType = subType;
    }

    @ColumnId(name = "ACTIVITY_DATE")
    protected Date activityDate;

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate){
        this.activityDate = activityDate;
    }

    @ColumnId(name = "ACTIVITY_TIME")
    protected Date activityTime;

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date activityTime){
        this.activityTime = activityTime;
    }

    @ColumnId(name = "VALUE")
    protected BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value){
        this.value = value;
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
