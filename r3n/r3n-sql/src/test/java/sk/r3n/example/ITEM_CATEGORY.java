package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class ITEM_CATEGORY implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.ITEM_CATEGORY(), DataType.LONG);
    }
    public static Column BILL_ITEM_FK() {
        return new Column("bill_item_fk", TABLE.ITEM_CATEGORY(), DataType.LONG);
    }
    public static Column EXPENSE_CATEGORY_FK() {
        return new Column("expense_category_fk", TABLE.ITEM_CATEGORY(), DataType.LONG);
    }

}
