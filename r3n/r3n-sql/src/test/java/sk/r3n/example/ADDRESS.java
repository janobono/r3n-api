package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class ADDRESS implements Serializable {

    public static Column ID() {
        return new Column("ID", TABLE.ADDRESS(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("ID", TABLE.ADDRESS(alias), DataType.LONG);
    }

    public static Column PERSON_FK() {
        return new Column("PERSON_FK", TABLE.ADDRESS(), DataType.LONG);
    }

    public static Column PERSON_FK(String alias) {
        return new Column("PERSON_FK", TABLE.ADDRESS(alias), DataType.LONG);
    }

    public static Column TYPE() {
        return new Column("TYPE", TABLE.ADDRESS(), DataType.SHORT);
    }

    public static Column TYPE(String alias) {
        return new Column("TYPE", TABLE.ADDRESS(alias), DataType.SHORT);
    }

    public static Column STREET() {
        return new Column("STREET", TABLE.ADDRESS(), DataType.STRING);
    }

    public static Column STREET(String alias) {
        return new Column("STREET", TABLE.ADDRESS(alias), DataType.STRING);
    }

    public static Column CITY() {
        return new Column("CITY", TABLE.ADDRESS(), DataType.STRING);
    }

    public static Column CITY(String alias) {
        return new Column("CITY", TABLE.ADDRESS(alias), DataType.STRING);
    }

    public static Column STATE() {
        return new Column("STATE", TABLE.ADDRESS(), DataType.STRING);
    }

    public static Column STATE(String alias) {
        return new Column("STATE", TABLE.ADDRESS(alias), DataType.STRING);
    }

    public static Column POST_CODE() {
        return new Column("POST_CODE", TABLE.ADDRESS(), DataType.STRING);
    }

    public static Column POST_CODE(String alias) {
        return new Column("POST_CODE", TABLE.ADDRESS(alias), DataType.STRING);
    }

    public static Column TEST_TIME() {
        return new Column("TEST_TIME", TABLE.ADDRESS(), DataType.TIME);
    }

    public static Column TEST_TIME(String alias) {
        return new Column("TEST_TIME", TABLE.ADDRESS(alias), DataType.TIME);
    }

    public static Column TEST_BLOB() {
        return new Column("TEST_BLOB", TABLE.ADDRESS(), DataType.BLOB);
    }

    public static Column TEST_BLOB(String alias) {
        return new Column("TEST_BLOB", TABLE.ADDRESS(alias), DataType.BLOB);
    }

    public static Column VALUE() {
        return new Column("VALUE", TABLE.ADDRESS(), DataType.BIG_DECIMAL);
    }

    public static Column VALUE(String alias) {
        return new Column("VALUE", TABLE.ADDRESS(alias), DataType.BIG_DECIMAL);
    }

    public static Column[] columns() {
        return new Column[]{ID(), PERSON_FK(), TYPE(), STREET(), CITY(), STATE(), POST_CODE(), TEST_TIME(), TEST_BLOB(), VALUE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), PERSON_FK(alias), TYPE(alias), STREET(alias), CITY(alias), STATE(alias), POST_CODE(alias), TEST_TIME(alias), TEST_BLOB(alias), VALUE(alias)};
    }
}
