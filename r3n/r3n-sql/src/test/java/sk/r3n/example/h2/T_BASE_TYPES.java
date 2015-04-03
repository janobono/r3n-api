package sk.r3n.example.h2;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class T_BASE_TYPES implements Serializable {

    public static Column ID() {
        return new Column("ID", TABLE.T_BASE_TYPES(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("ID", TABLE.T_BASE_TYPES(alias), DataType.LONG);
    }

    public static Column T_SHORT_SMALLINT() {
        return new Column("T_SHORT_SMALLINT", TABLE.T_BASE_TYPES(), DataType.SHORT);
    }

    public static Column T_SHORT_SMALLINT(String alias) {
        return new Column("T_SHORT_SMALLINT", TABLE.T_BASE_TYPES(alias), DataType.SHORT);
    }

    public static Column T_SHORT_TINYINT() {
        return new Column("T_SHORT_TINYINT", TABLE.T_BASE_TYPES(), DataType.SHORT);
    }

    public static Column T_SHORT_TINYINT(String alias) {
        return new Column("T_SHORT_TINYINT", TABLE.T_BASE_TYPES(alias), DataType.SHORT);
    }

    public static Column T_INTEGER() {
        return new Column("T_INTEGER", TABLE.T_BASE_TYPES(), DataType.INTEGER);
    }

    public static Column T_INTEGER(String alias) {
        return new Column("T_INTEGER", TABLE.T_BASE_TYPES(alias), DataType.INTEGER);
    }

    public static Column T_LONG() {
        return new Column("T_LONG", TABLE.T_BASE_TYPES(), DataType.LONG);
    }

    public static Column T_LONG(String alias) {
        return new Column("T_LONG", TABLE.T_BASE_TYPES(alias), DataType.LONG);
    }

    public static Column T_BIG_DECIMAL() {
        return new Column("T_BIG_DECIMAL", TABLE.T_BASE_TYPES(), DataType.BIG_DECIMAL);
    }

    public static Column T_BIG_DECIMAL(String alias) {
        return new Column("T_BIG_DECIMAL", TABLE.T_BASE_TYPES(alias), DataType.BIG_DECIMAL);
    }

    public static Column T_STRING() {
        return new Column("T_STRING", TABLE.T_BASE_TYPES(), DataType.STRING);
    }

    public static Column T_STRING(String alias) {
        return new Column("T_STRING", TABLE.T_BASE_TYPES(alias), DataType.STRING);
    }

    public static Column T_STRING_SCDF() {
        return new Column("T_STRING_SCDF", TABLE.T_BASE_TYPES(), DataType.STRING);
    }

    public static Column T_STRING_SCDF(String alias) {
        return new Column("T_STRING_SCDF", TABLE.T_BASE_TYPES(alias), DataType.STRING);
    }

    public static Column T_BLOB() {
        return new Column("T_BLOB", TABLE.T_BASE_TYPES(), DataType.BLOB);
    }

    public static Column T_BLOB(String alias) {
        return new Column("T_BLOB", TABLE.T_BASE_TYPES(alias), DataType.BLOB);
    }

    public static Column T_TIME_STAMP() {
        return new Column("T_TIME_STAMP", TABLE.T_BASE_TYPES(), DataType.TIME_STAMP);
    }

    public static Column T_TIME_STAMP(String alias) {
        return new Column("T_TIME_STAMP", TABLE.T_BASE_TYPES(alias), DataType.TIME_STAMP);
    }

    public static Column T_TIME() {
        return new Column("T_TIME", TABLE.T_BASE_TYPES(), DataType.TIME);
    }

    public static Column T_TIME(String alias) {
        return new Column("T_TIME", TABLE.T_BASE_TYPES(alias), DataType.TIME);
    }

    public static Column T_DATE() {
        return new Column("T_DATE", TABLE.T_BASE_TYPES(), DataType.DATE);
    }

    public static Column T_DATE(String alias) {
        return new Column("T_DATE", TABLE.T_BASE_TYPES(alias), DataType.DATE);
    }

    public static Column T_BOOLEAN() {
        return new Column("T_BOOLEAN", TABLE.T_BASE_TYPES(), DataType.BOOLEAN);
    }

    public static Column T_BOOLEAN(String alias) {
        return new Column("T_BOOLEAN", TABLE.T_BASE_TYPES(alias), DataType.BOOLEAN);
    }

    public static Column[] columns() {
        return new Column[]{ID(), T_SHORT_SMALLINT(), T_SHORT_TINYINT(), T_INTEGER(), T_LONG(), T_BIG_DECIMAL(), T_STRING(), T_STRING_SCDF(), T_BLOB(), T_TIME_STAMP(), T_TIME(), T_DATE(), T_BOOLEAN()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), T_SHORT_SMALLINT(alias), T_SHORT_TINYINT(alias), T_INTEGER(alias), T_LONG(alias), T_BIG_DECIMAL(alias), T_STRING(alias), T_STRING_SCDF(alias), T_BLOB(alias), T_TIME_STAMP(alias), T_TIME(alias), T_DATE(alias), T_BOOLEAN(alias)};
    }
}
