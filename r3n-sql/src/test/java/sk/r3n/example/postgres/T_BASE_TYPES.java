package sk.r3n.example.postgres;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class T_BASE_TYPES implements Serializable {

    public static Column ID() {
        return new Column("id", TABLE.T_BASE_TYPES(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("id", TABLE.T_BASE_TYPES(alias), DataType.LONG);
    }

    public static Column T_SHORT() {
        return new Column("t_short", TABLE.T_BASE_TYPES(), DataType.SHORT);
    }

    public static Column T_SHORT(String alias) {
        return new Column("t_short", TABLE.T_BASE_TYPES(alias), DataType.SHORT);
    }

    public static Column T_INTEGER() {
        return new Column("t_integer", TABLE.T_BASE_TYPES(), DataType.INTEGER);
    }

    public static Column T_INTEGER(String alias) {
        return new Column("t_integer", TABLE.T_BASE_TYPES(alias), DataType.INTEGER);
    }

    public static Column T_LONG() {
        return new Column("t_long", TABLE.T_BASE_TYPES(), DataType.LONG);
    }

    public static Column T_LONG(String alias) {
        return new Column("t_long", TABLE.T_BASE_TYPES(alias), DataType.LONG);
    }

    public static Column T_BIG_DECIMAL() {
        return new Column("t_big_decimal", TABLE.T_BASE_TYPES(), DataType.BIG_DECIMAL);
    }

    public static Column T_BIG_DECIMAL(String alias) {
        return new Column("t_big_decimal", TABLE.T_BASE_TYPES(alias), DataType.BIG_DECIMAL);
    }

    public static Column T_STRING_CHAR() {
        return new Column("t_string_char", TABLE.T_BASE_TYPES(), DataType.STRING);
    }

    public static Column T_STRING_CHAR(String alias) {
        return new Column("t_string_char", TABLE.T_BASE_TYPES(alias), DataType.STRING);
    }

    public static Column T_STRING_TEXT() {
        return new Column("t_string_text", TABLE.T_BASE_TYPES(), DataType.STRING);
    }

    public static Column T_STRING_TEXT(String alias) {
        return new Column("t_string_text", TABLE.T_BASE_TYPES(alias), DataType.STRING);
    }

    public static Column T_STRING_VARCHAR() {
        return new Column("t_string_varchar", TABLE.T_BASE_TYPES(), DataType.STRING);
    }

    public static Column T_STRING_VARCHAR(String alias) {
        return new Column("t_string_varchar", TABLE.T_BASE_TYPES(alias), DataType.STRING);
    }

    public static Column T_STRING_SCDF() {
        return new Column("t_string_scdf", TABLE.T_BASE_TYPES(), DataType.STRING);
    }

    public static Column T_STRING_SCDF(String alias) {
        return new Column("t_string_scdf", TABLE.T_BASE_TYPES(alias), DataType.STRING);
    }

    public static Column T_BLOB() {
        return new Column("t_blob", TABLE.T_BASE_TYPES(), DataType.BLOB);
    }

    public static Column T_BLOB(String alias) {
        return new Column("t_blob", TABLE.T_BASE_TYPES(alias), DataType.BLOB);
    }

    public static Column T_TIME_STAMP() {
        return new Column("t_time_stamp", TABLE.T_BASE_TYPES(), DataType.TIME_STAMP);
    }

    public static Column T_TIME_STAMP(String alias) {
        return new Column("t_time_stamp", TABLE.T_BASE_TYPES(alias), DataType.TIME_STAMP);
    }

    public static Column T_TIME() {
        return new Column("t_time", TABLE.T_BASE_TYPES(), DataType.TIME);
    }

    public static Column T_TIME(String alias) {
        return new Column("t_time", TABLE.T_BASE_TYPES(alias), DataType.TIME);
    }

    public static Column T_DATE() {
        return new Column("t_date", TABLE.T_BASE_TYPES(), DataType.DATE);
    }

    public static Column T_DATE(String alias) {
        return new Column("t_date", TABLE.T_BASE_TYPES(alias), DataType.DATE);
    }

    public static Column T_BOOLEAN() {
        return new Column("t_boolean", TABLE.T_BASE_TYPES(), DataType.BOOLEAN);
    }

    public static Column T_BOOLEAN(String alias) {
        return new Column("t_boolean", TABLE.T_BASE_TYPES(alias), DataType.BOOLEAN);
    }

    public static Column[] columns() {
        return new Column[]{ID(), T_SHORT(), T_INTEGER(), T_LONG(), T_BIG_DECIMAL(), T_STRING_CHAR(), T_STRING_TEXT(), T_STRING_VARCHAR(), T_STRING_SCDF(), T_BLOB(), T_TIME_STAMP(), T_TIME(), T_DATE(), T_BOOLEAN()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), T_SHORT(alias), T_INTEGER(alias), T_LONG(alias), T_BIG_DECIMAL(alias), T_STRING_CHAR(alias), T_STRING_TEXT(alias), T_STRING_VARCHAR(alias), T_STRING_SCDF(alias), T_BLOB(alias), T_TIME_STAMP(alias), T_TIME(alias), T_DATE(alias), T_BOOLEAN(alias)};
    }
}
