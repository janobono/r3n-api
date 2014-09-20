package sk.r3n.example.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "ITEM_CATEGORY")
public class ItemCategory implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "BILL_ITEM_FK")
    protected Long billItemFk;

    public Long getBillItemFk() {
        return billItemFk;
    }

    public void setBillItemFk(Long billItemFk){
        this.billItemFk = billItemFk;
    }

    @ColumnId(name = "EXPENSE_CATEGORY_FK")
    protected Long expenseCategoryFk;

    public Long getExpenseCategoryFk() {
        return expenseCategoryFk;
    }

    public void setExpenseCategoryFk(Long expenseCategoryFk){
        this.expenseCategoryFk = expenseCategoryFk;
    }

}
