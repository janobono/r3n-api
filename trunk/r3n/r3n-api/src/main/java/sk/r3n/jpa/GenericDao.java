package sk.r3n.jpa;

import java.util.List;
import java.util.Map;

public interface GenericDao {

    public <T> T create(T t);

    public <T> T update(T t);

    public <T, PK> void delete(Class<T> type, PK id);

    public <T, PK> T get(Class<T> type, PK id);

    public <T> List<T> getAll(Class<T> type);

    public <T> List<T> findByNamedQuery(Class<T> clazz, String namedQuery);

    public <T> List<T> findByNamedQuery(Class<T> clazz, String namedQuery, Map<String, Object> parameters);

    public <T> List<T> findByNamedQuery(Class<T> clazz, String namedQuery, int pageSize, int page,
            Map<String, Object> parameters);

    public <T> List<T> findByNamedQuery(Class<T> clazz, String namedQuery, int pageSize, int page);

    public <R> R getSingleResultByNamedQuery(Class<R> clazz, String namedQuery, Map<String, Object> parameters);

}
