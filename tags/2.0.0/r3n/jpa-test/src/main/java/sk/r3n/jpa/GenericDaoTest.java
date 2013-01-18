package sk.r3n.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class GenericDaoTest extends GenericDaoImpl {

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