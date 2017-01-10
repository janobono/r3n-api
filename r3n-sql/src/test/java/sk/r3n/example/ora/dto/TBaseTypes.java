package sk.r3n.example.ora.dto;

import java.io.Serializable;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import sk.r3n.dto.ColumnId;

public class TBaseTypes implements Serializable {

    @ColumnId(table = "t_base_types", column = "id")
    protected Long id;

    @ColumnId(table = "t_base_types", column = "t_short")
    protected Short tShort;

    @ColumnId(table = "t_base_types", column = "t_integer")
    protected Integer tInteger;

    @ColumnId(table = "t_base_types", column = "t_long")
    protected Long tLong;

    @ColumnId(table = "t_base_types", column = "t_big_decimal")
    protected BigDecimal tBigDecimal;

    @ColumnId(table = "t_base_types", column = "t_string_char")
    protected String tStringChar;

    @ColumnId(table = "t_base_types", column = "t_string_clob")
    protected String tStringClob;

    @ColumnId(table = "t_base_types", column = "t_string_varchar2")
    protected String tStringVarchar2;

    @ColumnId(table = "t_base_types", column = "t_string_scdf")
    protected String tStringScdf;

    @ColumnId(table = "t_base_types", column = "t_blob")
    protected File tBlob;

    @ColumnId(table = "t_base_types", column = "t_time_stamp")
    protected Date tTimeStamp;

    @ColumnId(table = "t_base_types", column = "t_date")
    protected Date tDate;

    @ColumnId(table = "t_base_types", column = "t_boolean")
    protected Boolean tBoolean;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getTShort() {
        return tShort;
    }

    public void setTShort(Short tShort) {
        this.tShort = tShort;
    }

    public Integer getTInteger() {
        return tInteger;
    }

    public void setTInteger(Integer tInteger) {
        this.tInteger = tInteger;
    }

    public Long getTLong() {
        return tLong;
    }

    public void setTLong(Long tLong) {
        this.tLong = tLong;
    }

    public BigDecimal getTBigDecimal() {
        return tBigDecimal;
    }

    public void setTBigDecimal(BigDecimal tBigDecimal) {
        this.tBigDecimal = tBigDecimal;
    }

    public String getTStringChar() {
        return tStringChar;
    }

    public void setTStringChar(String tStringChar) {
        this.tStringChar = tStringChar;
    }

    public String getTStringClob() {
        return tStringClob;
    }

    public void setTStringClob(String tStringClob) {
        this.tStringClob = tStringClob;
    }

    public String getTStringVarchar2() {
        return tStringVarchar2;
    }

    public void setTStringVarchar2(String tStringVarchar2) {
        this.tStringVarchar2 = tStringVarchar2;
    }

    public String getTStringScdf() {
        return tStringScdf;
    }

    public void setTStringScdf(String tStringScdf) {
        this.tStringScdf = tStringScdf;
    }

    public File getTBlob() {
        return tBlob;
    }

    public void setTBlob(File tBlob) {
        this.tBlob = tBlob;
    }

    public Date getTTimeStamp() {
        return tTimeStamp;
    }

    public void setTTimeStamp(Date tTimeStamp) {
        this.tTimeStamp = tTimeStamp;
    }

    public Date getTDate() {
        return tDate;
    }

    public void setTDate(Date tDate) {
        this.tDate = tDate;
    }

    public Boolean getTBoolean() {
        return tBoolean;
    }

    public void setTBoolean(Boolean tBoolean) {
        this.tBoolean = tBoolean;
    }

}
