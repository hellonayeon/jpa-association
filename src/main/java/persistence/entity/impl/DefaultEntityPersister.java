package persistence.entity.impl;

import java.lang.reflect.Field;
import jdbc.JdbcTemplate;
import persistence.entity.EntityId;
import persistence.entity.EntityPersister;
import persistence.meta.SchemaMeta;
import persistence.sql.dml.query.UpdateQuery;
import persistence.sql.dml.query.builder.DeleteQueryBuilder;
import persistence.sql.dml.query.builder.InsertQueryBuilder;
import persistence.sql.dml.query.builder.UpdateQueryBuilder;

public class DefaultEntityPersister implements EntityPersister {

    private final JdbcTemplate jdbcTemplate;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> Object insert(T entity) {
        SchemaMeta schemaMeta = new SchemaMeta(entity);
        String queryString = InsertQueryBuilder.builder()
                .insert(schemaMeta.tableName(), schemaMeta.columnNamesWithoutPrimaryKey())
                .values(schemaMeta.columnValuesWithoutPrimaryKey())
                .build();
        Object id = jdbcTemplate.insertAndGetPrimaryKey(queryString);

        updateEntityId(entity, id);
        return entity;
    }


    private <T> void updateEntityId(T entity, Object id) {
        Field idField = EntityId.getIdField(entity);
        idField.setAccessible(true);
        try {
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void update(T entity) {
        UpdateQuery query = new UpdateQuery(entity);
        String queryString = UpdateQueryBuilder.builder()
                        .update(query.tableName())
                        .set(query.columns())
                        .build();
        jdbcTemplate.execute(queryString);
    }

    @Override
    public <T> void delete(T entity) {
        SchemaMeta schemaMeta = new SchemaMeta(entity);
        String query = DeleteQueryBuilder.builder()
                .delete(schemaMeta.tableName())
                .build();
        jdbcTemplate.execute(query);
    }

}
