package sk.r3n.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "DOMAIN")
@NamedQueries({
    @NamedQuery(name = Domain.DOMAIN_BY_CODE, query = "SELECT d FROM Domain d WHERE d.code = :code")
})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Domain extends BaseEntity implements Serializable {

    public static final String DOMAIN_BY_CODE = "DOMAIN_BY_CODE";

    @Column(name = "NAME", nullable = false, updatable = true)
    private String name;

    @Column(name = "CODE", nullable = false, updatable = true)
    private String code;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, targetEntity = Subdomain.class, mappedBy = "domain")
    private List<Subdomain> subdomains;

    public Domain() {
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

    public List<Subdomain> getSubdomains() {
        if (subdomains == null) {
            subdomains = new ArrayList<>();
        }
        return subdomains;
    }

    public void setSubdomains(List<Subdomain> subdomains) {
        this.subdomains = subdomains;
    }

}