package persistence.entity.loader;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.EntityIdExtractor;
import persistence.meta.RelationMeta;
import persistence.meta.ColumnMeta;
import persistence.sql.dml.query.WhereCondition;
import persistence.sql.dml.query.WhereOperator;
import persistence.sql.dml.query.builder.SelectQueryBuilder;

public class EntityCollectionLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityCollectionLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T loadCollection(Class<T> clazz, Object instance) {
        List<ColumnMeta> columnMetas = getColumnMetaHasRelation(clazz);
        for (ColumnMeta columnMeta : columnMetas) {
            RelationMeta relationMeta = columnMeta.relationMeta();
            String query = SelectQueryBuilder.builder()
                    .select()
                    .from(relationMeta.getJoinTableName())
                    .where(List.of(
                            new WhereCondition(
                                    relationMeta.getJoinColumnName(),
                                    WhereOperator.EQUAL,
                                    EntityIdExtractor.extractIdValue(instance)))
                    )
                    .build();

            List<?> children = jdbcTemplate.query(query, new EntityRowMapper<>(relationMeta.getJoinColumnType()));
            mapChildrenField(instance, columnMeta, children);
        }

        return clazz.cast(instance);
    }

    private List<ColumnMeta> getColumnMetaHasRelation(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> new ColumnMeta(field, clazz))
                .filter(columnMeta -> columnMeta.relationMeta().hasRelation())
                .toList();
    }

    private void mapChildrenField(Object instance, ColumnMeta columnMeta, List<?> children) {
        try {
            Field field = columnMeta.field();
            field.setAccessible(true);
            field.set(instance, children);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
