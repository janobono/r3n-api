package sk.r3n.example.dto;

import java.io.Serializable;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "item_category")
public class ItemCategory implements Serializable {

    @ColumnId(name = "id")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "bill_item_fk")
    protected Long billItemFk;

    public Long getBillItemFk() {
        return billItemFk;
    }

    public void setBillItemFk(Long billItemFk){
        this.billItemFk = billItemFk;
    }

    @ColumnId(name = "expense_category_fk")
    protected Long expenseCategoryFk;

    public Long getExpenseCategoryFk() {
        return expenseCategoryFk;
    }

    public void setExpenseCategoryFk(Long expenseCategoryFk){
        this.expenseCategoryFk = expenseCategoryFk;
    }

}
