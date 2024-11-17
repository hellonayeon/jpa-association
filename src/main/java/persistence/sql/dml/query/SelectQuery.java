package persistence.sql.dml.query;

import static persistence.validator.AnnotationValidator.isNotPresent;

import jakarta.persistence.Transient;
import java.util.Arrays;
import java.util.List;
import persistence.sql.metadata.ColumnName;
import persistence.sql.metadata.TableAlias;
import persistence.sql.metadata.TableName;

public record SelectQuery(TableName tableName,
                          TableAlias tableAlias,
                          List<ColumnName> columnNames) {

    public SelectQuery(Class<?> clazz) {
        this(
                new TableName(clazz),
                new TableAlias(new TableName(clazz)),
                Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> isNotPresent(field, Transient.class))
                        .map(field -> new ColumnName(field, new TableName(clazz)))
                        .toList()
        );
    }

}
