package sk.r3n.dao;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.*;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @SequenceGenerator(name = "testIdGenerator", sequenceName = "TEST_ID_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "testIdGenerator")
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Column(name = "DATE_CREATED", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateCreated;

    @Column(name = "DATE_MODIFIED", nullable = true, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar dateModified;

    @Column(name = "DELETED", nullable = false, updatable = true)
    private Boolean deleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Calendar getDateModified() {
        return dateModified;
    }

    public void setDateModified(Calendar dateModified) {
        this.dateModified = dateModified;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
