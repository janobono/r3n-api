package sk.r3n.example.dto;

import java.io.Serializable;
import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import sk.r3n.dto.TableId;
import sk.r3n.dto.ColumnId;

@TableId(name = "ADDRESS")
public class Address implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    @ColumnId(name = "PERSON_FK")
    protected Long personFk;

    @ColumnId(name = "TYPE")
    protected Short type;

    @ColumnId(name = "STREET")
    protected String street;

    @ColumnId(name = "CITY")
    protected String city;

    @ColumnId(name = "STATE")
    protected String state;

    @ColumnId(name = "POST_CODE")
    protected String postCode;

    @ColumnId(name = "TEST_TIME")
    protected Date testTime;

    @ColumnId(name = "TEST_BLOB")
    protected File testBlob;

    @ColumnId(name = "VALUE")
    protected BigDecimal value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonFk() {
        return personFk;
    }

    public void setPersonFk(Long personFk) {
        this.personFk = personFk;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public Date getTestTime() {
        return testTime;
    }

    public void setTestTime(Date testTime) {
        this.testTime = testTime;
    }

    public File getTestBlob() {
        return testBlob;
    }

    public void setTestBlob(File testBlob) {
        this.testBlob = testBlob;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Address{" + "id=" + id + ", personFk=" + personFk + ", type=" + type + ", street=" + street
                + ", city=" + city + ", state=" + state + ", postCode=" + postCode + ", testTime=" + testTime
                + ", testBlob=" + testBlob + ", value=" + value + '}';
    }

}
