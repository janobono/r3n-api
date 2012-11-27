package sk.r3n.jpa;

import javax.persistence.EntityManager;
import org.junit.Test;

public class GenericDaoTest {

    private class TestGenericDaoImpl extends GenericDaoImpl  {

        @Override
        protected EntityManager getEntityManager() {
            throw new UnsupportedOperationException("Not supported yet.");
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
        //TODO.
    }

}
