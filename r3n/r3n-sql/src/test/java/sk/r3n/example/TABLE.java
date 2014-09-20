package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Table;

public class TABLE implements Serializable {

    public static Table EXPENSE_CATEGORY() {
        return new Table("EXPENSE_CATEGORY", "T1");
    }

    public static Table EXPENSE_CATEGORY(String alias) {
        return new Table("EXPENSE_CATEGORY", alias);
    }

    public static Table BILL() {
        return new Table("BILL", "T2");
    }

    public static Table BILL(String alias) {
        return new Table("BILL", alias);
    }

    public static Table BILL_ACTIVITY() {
        return new Table("BILL_ACTIVITY", "T3");
    }

    public static Table BILL_ACTIVITY(String alias) {
        return new Table("BILL_ACTIVITY", alias);
    }

    public static Table H_PROPERTIES() {
        return new Table("H_PROPERTIES", "T4");
    }

    public static Table H_PROPERTIES(String alias) {
        return new Table("H_PROPERTIES", alias);
    }

    public static Table BILL_ITEM() {
        return new Table("BILL_ITEM", "T5");
    }

    public static Table BILL_ITEM(String alias) {
        return new Table("BILL_ITEM", alias);
    }

    public static Table ITEM_CATEGORY() {
        return new Table("ITEM_CATEGORY", "T6");
    }

    public static Table ITEM_CATEGORY(String alias) {
        return new Table("ITEM_CATEGORY", alias);
    }

    public static Table ACCOUNT_ACTIVITY() {
        return new Table("ACCOUNT_ACTIVITY", "T7");
    }

    public static Table ACCOUNT_ACTIVITY(String alias) {
        return new Table("ACCOUNT_ACTIVITY", alias);
    }

    public static Table ACCOUNT() {
        return new Table("ACCOUNT", "T8");
    }

    public static Table ACCOUNT(String alias) {
        return new Table("ACCOUNT", alias);
    }

}
