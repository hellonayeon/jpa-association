package persistence.meta;

import static persistence.validator.AnnotationValidator.isNotPresent;

import jakarta.persistence.Transient;
import java.util.Arrays;
import java.util.List;
import persistence.entity.Relation;
import persistence.sql.ddl.query.constraint.PrimaryKeyConstraint;

public class SchemaMeta {

    private final Class<?> clazz;
    private final List<ColumnMeta> columnMetas;
    private final TableMeta tableMeta;
    private final PrimaryKeyConstraint primaryKeyConstraint;

    public SchemaMeta(Class<?> clazz) {
        this(
                clazz,
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> isNotPresent(field, Transient.class))
                        .map(field -> new ColumnMeta(field, clazz))
                        .toList(),
                new TableMeta(clazz),
                PrimaryKeyConstraint.from(clazz)
        );
    }

    private SchemaMeta(Class<?> clazz, List<ColumnMeta> columnMetas, TableMeta tableMeta, PrimaryKeyConstraint primaryKeyConstraint) {
        this.clazz = clazz;
        this.columnMetas = columnMetas;
        this.tableMeta = tableMeta;
        this.primaryKeyConstraint = primaryKeyConstraint;
    }

    private boolean hasRelation() {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> new ColumnMeta(field, clazz))
                .map(ColumnMeta::relation)
                .anyMatch(Relation::hasRelation);
    }

    public boolean hasNotRelation() {
        return !hasRelation();
    }

    public String tableName() {
        return tableMeta.name();
    }

    public String primaryKeyColumnName() {
        return primaryKeyConstraint.column().name();
    }

    public List<String> columnNames() {
        return columnMetas.stream()
                .map(ColumnMeta::name)
                .toList();
    }

}
