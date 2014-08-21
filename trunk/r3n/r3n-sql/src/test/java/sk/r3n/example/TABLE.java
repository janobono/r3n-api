package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;

public class TABLE implements Serializable {

    public static Table H_PROPERTIES() {
        return new Table("h_properties", "T1");
    }
    public static Table ACCOUNT() {
        return new Table("account", "T2");
    }
    public static Table BILL() {
        return new Table("bill", "T3");
    }
    public static Table ACCOUNT_ACTIVITY() {
        return new Table("account_activity", "T4");
    }
    public static Table BILL_ACTIVITY() {
        return new Table("bill_activity", "T5");
    }
    public static Table BILL_ITEM() {
        return new Table("bill_item", "T6");
    }
    public static Table EXPENSE_CATEGORY() {
        return new Table("expense_category", "T7");
    }
    public static Table ITEM_CATEGORY() {
        return new Table("item_category", "T8");
    }

}
