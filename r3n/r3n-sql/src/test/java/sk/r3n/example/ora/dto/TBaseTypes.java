package sk.r3n.example.ora.dto;

import java.io.Serializable;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "T_BASE_TYPES")
public class TBaseTypes implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @ColumnId(name = "T_SHORT")
    protected Short tShort;

    public Short getTShort() {
        return tShort;
    }

    public void setTShort(Short tShort){
        this.tShort = tShort;
    }

    @ColumnId(name = "T_INTEGER")
    protected Integer tInteger;

    public Integer getTInteger() {
        return tInteger;
    }

    public void setTInteger(Integer tInteger){
        this.tInteger = tInteger;
    }

    @ColumnId(name = "T_LONG")
    protected Long tLong;

    public Long getTLong() {
        return tLong;
    }

    public void setTLong(Long tLong){
        this.tLong = tLong;
    }

    @ColumnId(name = "T_BIG_DECIMAL")
    protected BigDecimal tBigDecimal;

    public BigDecimal getTBigDecimal() {
        return tBigDecimal;
    }

    public void setTBigDecimal(BigDecimal tBigDecimal){
        this.tBigDecimal = tBigDecimal;
    }

    @ColumnId(name = "T_STRING_CHAR")
    protected String tStringChar;

    public String getTStringChar() {
        return tStringChar;
    }

    public void setTStringChar(String tStringChar){
        this.tStringChar = tStringChar;
    }

    @ColumnId(name = "T_STRING_CLOB")
    protected String tStringClob;

    public String getTStringClob() {
        return tStringClob;
    }

    public void setTStringClob(String tStringClob){
        this.tStringClob = tStringClob;
    }

    @ColumnId(name = "T_STRING_VARCHAR2")
    protected String tStringVarchar2;

    public String getTStringVarchar2() {
        return tStringVarchar2;
    }

    public void setTStringVarchar2(String tStringVarchar2){
        this.tStringVarchar2 = tStringVarchar2;
    }

    @ColumnId(name = "T_STRING_SCDF")
    protected String tStringScdf;

    public String getTStringScdf() {
        return tStringScdf;
    }

    public void setTStringScdf(String tStringScdf){
        this.tStringScdf = tStringScdf;
    }

    @ColumnId(name = "T_BLOB")
    protected File tBlob;

    public File getTBlob() {
        return tBlob;
    }

    public void setTBlob(File tBlob){
        this.tBlob = tBlob;
    }

    @ColumnId(name = "T_TIME_STAMP")
    protected Date tTimeStamp;

    public Date getTTimeStamp() {
        return tTimeStamp;
    }

    public void setTTimeStamp(Date tTimeStamp){
        this.tTimeStamp = tTimeStamp;
    }

    @ColumnId(name = "T_DATE")
    protected Date tDate;

    public Date getTDate() {
        return tDate;
    }

    public void setTDate(Date tDate){
        this.tDate = tDate;
    }

    @ColumnId(name = "T_BOOLEAN")
    protected Boolean tBoolean;

    public Boolean getTBoolean() {
        return tBoolean;
    }

    public void setTBoolean(Boolean tBoolean){
        this.tBoolean = tBoolean;
    }

}
