package sk.r3n.jpa;

import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "SUBDOMAIN")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Subdomain extends BaseEntity implements Serializable {

    public static final String SUBDOMAIN_BY_CODE = "SUBDOMAIN_BY_CODE";

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Domain.class, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "DOMAIN_FK", nullable = false, updatable = true)
    private Domain domain;

    @Column(name = "NAME", nullable = false, updatable = true)
    private String name;

    @Column(name = "CODE", nullable = false, updatable = true)
    private String code;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}