package sk.r3n.example;

import java.io.Serializable;
import sk.r3n.sql.Column;
import sk.r3n.sql.DataType;

public class PERSON implements Serializable {

    public static Column ID() {
        return new Column("ID", TABLE.PERSON(), DataType.LONG);
    }

    public static Column ID(String alias) {
        return new Column("ID", TABLE.PERSON(alias), DataType.LONG);
    }

    public static Column CREATED() {
        return new Column("CREATED", TABLE.PERSON(), DataType.TIME_STAMP);
    }

    public static Column CREATED(String alias) {
        return new Column("CREATED", TABLE.PERSON(alias), DataType.TIME_STAMP);
    }

    public static Column CREATOR() {
        return new Column("CREATOR", TABLE.PERSON(), DataType.STRING);
    }

    public static Column CREATOR(String alias) {
        return new Column("CREATOR", TABLE.PERSON(alias), DataType.STRING);
    }

    public static Column TYPE() {
        return new Column("TYPE", TABLE.PERSON(), DataType.SHORT);
    }

    public static Column TYPE(String alias) {
        return new Column("TYPE", TABLE.PERSON(alias), DataType.SHORT);
    }

    public static Column PERSONAL_ID() {
        return new Column("PERSONAL_ID", TABLE.PERSON(), DataType.STRING);
    }

    public static Column PERSONAL_ID(String alias) {
        return new Column("PERSONAL_ID", TABLE.PERSON(alias), DataType.STRING);
    }

    public static Column FIRST_NAME() {
        return new Column("FIRST_NAME", TABLE.PERSON(), DataType.STRING);
    }

    public static Column FIRST_NAME(String alias) {
        return new Column("FIRST_NAME", TABLE.PERSON(alias), DataType.STRING);
    }

    public static Column FIRST_NAME_SCDF() {
        return new Column("FIRST_NAME_SCDF", TABLE.PERSON(), DataType.STRING);
    }

    public static Column FIRST_NAME_SCDF(String alias) {
        return new Column("FIRST_NAME_SCDF", TABLE.PERSON(alias), DataType.STRING);
    }

    public static Column LAST_NAME() {
        return new Column("LAST_NAME", TABLE.PERSON(), DataType.STRING);
    }

    public static Column LAST_NAME(String alias) {
        return new Column("LAST_NAME", TABLE.PERSON(alias), DataType.STRING);
    }

    public static Column LAST_NAME_SCDF() {
        return new Column("LAST_NAME_SCDF", TABLE.PERSON(), DataType.STRING);
    }

    public static Column LAST_NAME_SCDF(String alias) {
        return new Column("LAST_NAME_SCDF", TABLE.PERSON(alias), DataType.STRING);
    }

    public static Column BIRTH_DATE() {
        return new Column("BIRTH_DATE", TABLE.PERSON(), DataType.DATE);
    }

    public static Column BIRTH_DATE(String alias) {
        return new Column("BIRTH_DATE", TABLE.PERSON(alias), DataType.DATE);
    }

    public static Column NOTE() {
        return new Column("NOTE", TABLE.PERSON(), DataType.STRING);
    }

    public static Column NOTE(String alias) {
        return new Column("NOTE", TABLE.PERSON(alias), DataType.STRING);
    }

    public static Column[] columns() {
        return new Column[]{ID(), CREATED(), CREATOR(), TYPE(), PERSONAL_ID(), FIRST_NAME(), FIRST_NAME_SCDF(), LAST_NAME(), LAST_NAME_SCDF(), BIRTH_DATE(), NOTE()};
    }

    public static Column[] columns(String alias) {
        return new Column[]{ID(alias), CREATED(alias), CREATOR(alias), TYPE(alias), PERSONAL_ID(alias), FIRST_NAME(alias), FIRST_NAME_SCDF(alias), LAST_NAME(alias), LAST_NAME_SCDF(alias), BIRTH_DATE(alias), NOTE(alias)};
    }
}
