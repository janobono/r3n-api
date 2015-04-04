package sk.r3n.example.postgres.dto;

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

    @ColumnId(name = "T_SHORT")
    protected Short tShort;

    @ColumnId(name = "T_INTEGER")
    protected Integer tInteger;

    @ColumnId(name = "T_LONG")
    protected Long tLong;

    @ColumnId(name = "T_BIG_DECIMAL")
    protected BigDecimal tBigDecimal;

    @ColumnId(name = "T_STRING_CHAR")
    protected String tStringChar;

    @ColumnId(name = "T_STRING_TEXT")
    protected String tStringText;

    @ColumnId(name = "T_STRING_VARCHAR")
    protected String tStringVarchar;

    @ColumnId(name = "T_STRING_SCDF")
    protected String tStringScdf;

    @ColumnId(name = "T_BLOB")
    protected File tBlob;

    @ColumnId(name = "T_TIME_STAMP")
    protected Date tTimeStamp;

    @ColumnId(name = "T_TIME")
    protected Date tTime;

    @ColumnId(name = "T_DATE")
    protected Date tDate;

    @ColumnId(name = "T_BOOLEAN")
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

    public String getTStringText() {
        return tStringText;
    }

    public void setTStringText(String tStringText) {
        this.tStringText = tStringText;
    }

    public String getTStringVarchar() {
        return tStringVarchar;
    }

    public void setTStringVarchar(String tStringVarchar) {
        this.tStringVarchar = tStringVarchar;
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

    public Date getTTime() {
        return tTime;
    }

    public void setTTime(Date tTime) {
        this.tTime = tTime;
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

    @Override
    public String toString() {
        return "TBaseTypes{" + "id=" + id + ", tShort=" + tShort + ", tInteger=" + tInteger + ", tLong=" + tLong
                + ", tBigDecimal=" + tBigDecimal + ", tStringChar=" + tStringChar + ", tStringText=" + tStringText
                + ", tStringVarchar=" + tStringVarchar + ", tStringScdf=" + tStringScdf + ", tBlob=" + tBlob
                + ", tTimeStamp=" + tTimeStamp + ", tTime=" + tTime + ", tDate=" + tDate + ", tBoolean=" + tBoolean + '}';
    }

}
