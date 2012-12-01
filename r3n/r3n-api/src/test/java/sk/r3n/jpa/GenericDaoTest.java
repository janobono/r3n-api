package sk.r3n.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.junit.Test;

public class GenericDaoTest {

    private class TestGenericDaoImpl extends GenericDaoImpl {

        private EntityManager entityManager;

        @Override
        protected EntityManager getEntityManager() {
            if (entityManager == null) {
                EntityManagerFactory emf = Persistence.createEntityManagerFactory("TestOpenJPAPersistence");
                entityManager = emf.createEntityManager();
            }
            return entityManager;
        }

    }

    private GenericDao genericDao = new TestGenericDaoImpl();

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
