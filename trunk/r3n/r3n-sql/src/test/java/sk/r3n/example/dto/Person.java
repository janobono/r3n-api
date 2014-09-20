package sk.r3n.example.dto;

import java.io.Serializable;
import java.util.Date;
import sk.r3n.dto.ColumnId;
import sk.r3n.dto.TableId;

@TableId(name = "PERSON")
public class Person implements Serializable {

    @ColumnId(name = "ID")
    protected Long id;

    @ColumnId(name = "CREATED")
    protected Date created;

    @ColumnId(name = "CREATOR")
    protected String creator;

    @ColumnId(name = "TYPE")
    protected Short type;

    @ColumnId(name = "PERSONAL_ID")
    protected String personalId;

    @ColumnId(name = "FIRST_NAME")
    protected String firstName;

    @ColumnId(name = "FIRST_NAME_SCDF")
    protected String firstNameScdf;

    @ColumnId(name = "LAST_NAME")
    protected String lastName;

    @ColumnId(name = "LAST_NAME_SCDF")
    protected String lastNameScdf;

    @ColumnId(name = "BIRTH_DATE")
    protected Date birthDate;

    @ColumnId(name = "NOTE")
    protected String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public String getPersonalId() {
        return personalId;
    }

    public void setPersonalId(String personalId) {
        this.personalId = personalId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstNameScdf() {
        return firstNameScdf;
    }

    public void setFirstNameScdf(String firstNameScdf) {
        this.firstNameScdf = firstNameScdf;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastNameScdf() {
        return lastNameScdf;
    }

    public void setLastNameScdf(String lastNameScdf) {
        this.lastNameScdf = lastNameScdf;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "Person{" + "id=" + id + ", created=" + created + ", creator=" + creator + ", type=" + type
                + ", personalId=" + personalId + ", firstName=" + firstName + ", firstNameScdf=" + firstNameScdf
                + ", lastName=" + lastName + ", lastNameScdf=" + lastNameScdf + ", birthDate=" + birthDate
                + ", note=" + note + '}';
    }

}
