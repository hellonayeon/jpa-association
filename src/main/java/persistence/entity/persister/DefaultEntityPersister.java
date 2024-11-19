package persistence.entity.persister;

import static persistence.sql.dml.query.WhereOperator.EQUAL;

import java.lang.reflect.Field;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.EntityIdExtractor;
import persistence.meta.SchemaMeta;
import persistence.sql.dml.query.WhereCondition;
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
        Field idField = EntityIdExtractor.extractIdField(entity);
        idField.setAccessible(true);
        try {
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void update(T entity) {
        SchemaMeta schemaMeta = new SchemaMeta(entity);
        String query = UpdateQueryBuilder.builder()
                .update(schemaMeta.tableName())
                .set(schemaMeta.columnNamesWithoutPrimaryKey(), schemaMeta.columnValuesWithoutPrimaryKey())
                .where(List.of(new WhereCondition(schemaMeta.primaryKeyColumnName(), EQUAL, EntityIdExtractor.extractIdValue(entity))))
                .build();
        jdbcTemplate.execute(query);
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
