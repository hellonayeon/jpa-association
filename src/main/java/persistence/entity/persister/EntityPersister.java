package persistence.entity.persister;

public interface EntityPersister {

    <T> Object insert(T entity);
    <T> void update(T entity);
    <T> void delete(T entity);

}
