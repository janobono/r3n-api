package sk.r3n.jpa;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

public abstract class GenericDaoImpl implements GenericDao {

    protected abstract EntityManager getEntityManager();

    @Override
    public <T> T create(T t) {
        getEntityManager().persist(t);
        return t;
    }

    @Override
    public <T> T update(T t) {
        return getEntityManager().merge(t);
    }

    @Override
    public <T, PK> void delete(Class<T> clazz, PK id) {
        Object ref = getEntityManager().getReference(clazz, id);
        getEntityManager().remove(ref);
    }

    @Override
    public <T, PK> T get(Class<T> clazz, PK id) {
        return getEntityManager().find(clazz, id);
    }

    @Override
    public <T> List<T> getAll(Class<T> type) {
        return getEntityManager().createQuery("SELECT o FROM " + type.getName() + " o").getResultList();
    }

    @Override
    public <T> List<T> findByNamedQuery(Class<T> clazz, String queryName) {
        return getEntityManager().createNamedQuery(queryName, clazz).getResultList();
    }

    @Override
    public <T> List<T> findByNamedQuery(Class<T> clazz, String queryName, int pageSize, int page) {
        TypedQuery<T> query = getEntityManager().createNamedQuery(queryName, clazz);
        if (page > 1) {
            query.setFirstResult((page - 1) * pageSize);
        } else {
            query.setFirstResult(0);
        }
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public <T> List<T> findByNamedQuery(Class<T> clazz, String queryName, Map<String, Object> parameters) {
        return getQuery(clazz, queryName, parameters).getResultList();
    }

    @Override
    public <T> List<T> findByNamedQuery(Class<T> clazz, String queryName, int pageSize, int page,
            Map<String, Object> parameters) {
        TypedQuery<T> query = getQuery(clazz, queryName, parameters);
        if (page > 1) {
            query.setFirstResult((page - 1) * pageSize);
        } else {
            query.setFirstResult(0);
        }
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public <R> R getSingleResultByNamedQuery(Class<R> clazz, String queryName, Map<String, Object> parameters) {
        TypedQuery<R> query;
        if (parameters == null || parameters.isEmpty()) {
            query = getEntityManager().createNamedQuery(queryName, clazz);
        } else {
            query = getQuery(clazz, queryName, parameters);
        }
        return query.getSingleResult();
    }

    private <T> TypedQuery<T> getQuery(Class<T> clazz, String queryName, Map<String, Object> parameters) {
        Set<Entry<String, Object>> rawParameters = parameters.entrySet();
        TypedQuery<T> query = getEntityManager().createNamedQuery(queryName, clazz);
        for (Entry entry : rawParameters) {
            if (entry.getValue() instanceof TemporalParameter) {
                TemporalParameter temporalParameter = (TemporalParameter) entry.getValue();
                Calendar value = temporalParameter.getValue();
                TemporalType temporalType = temporalParameter.getTemporalType();
                query.setParameter((String) entry.getKey(), value, temporalType);
            } else {
                query.setParameter((String) entry.getKey(), (Object) entry.getValue());
            }
        }
        return query;
    }

}
