package sk.r3n.example.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "account_activity")
public class AccountActivity implements Serializable {

    @ColumnId(name = "id")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "account_fk")
    protected Long accountFk;

    public Long getAccountFk() {
        return accountFk;
    }

    public void setAccountFk(Long accountFk){
        this.accountFk = accountFk;
    }

    @ColumnId(name = "type")
    protected Short type;

    public Short getType() {
        return type;
    }

    public void setType(Short type){
        this.type = type;
    }

    @ColumnId(name = "sub_type")
    protected Short subType;

    public Short getSubType() {
        return subType;
    }

    public void setSubType(Short subType){
        this.subType = subType;
    }

    @ColumnId(name = "activity_date")
    protected Date activityDate;

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate){
        this.activityDate = activityDate;
    }

    @ColumnId(name = "activity_time")
    protected Date activityTime;

    public Date getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(Date activityTime){
        this.activityTime = activityTime;
    }

    @ColumnId(name = "value")
    protected BigDecimal value;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value){
        this.value = value;
    }

    @ColumnId(name = "note")
    protected String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note){
        this.note = note;
    }

}
