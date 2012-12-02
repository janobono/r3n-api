package sk.r3n.jpa;

import org.junit.Test;
import sk.r3n.dao.Domain;
import sk.r3n.dao.Subdomain;

public class JPAtest {

    private GenericDao genericDao = new GenericDaoTest();

    @Test
    public void testDao() {
        Domain domain = new Domain();
        domain.setCode("code");
        domain.setName("name");
        Subdomain subdomain = new Subdomain();
        subdomain.setCode("code");
        subdomain.setName("name");
        domain.getSubdomains().add(subdomain);
        genericDao.create(domain);
        genericDao.findByNamedQuery(Domain.class, Domain.DOMAIN_BY_CODE,
                QueryParameter.with("code", "code").parameters());
        //TODO.
    }

}
