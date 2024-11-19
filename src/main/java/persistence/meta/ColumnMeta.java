package persistence.meta;

import static persistence.validator.AnnotationValidator.isNotBlank;
import static persistence.validator.AnnotationValidator.isNotPresent;
import static persistence.validator.AnnotationValidator.isPresent;

import jakarta.persistence.Column;
import java.lang.reflect.Field;
import persistence.entity.Relation;
import persistence.sql.ddl.type.ColumnType;

public record ColumnMeta(Field field,
                         int type,
                         String name,
                         int length,
                         boolean nullable,
                         Relation relation) {

    private static final int DEFAULT_LENGTH = 255;

    public ColumnMeta(Field field, Class<?> clazz) {
        this(
                field,
                ColumnType.getSqlType(field.getType()),
                getName(field),
                getLength(field),
                getNullable(field),
                Relation.from(field)
        );
    }

    private static String getName(Field field) {
        if (isColumnNamePresent(field)) {
            return getColumnName(field);
        }
        return getFieldName(field);
    }

    private static boolean isColumnNamePresent(Field field) {
        return isPresent(field, Column.class) && isNotBlank(getColumnName(field));
    }

    private static String getColumnName(Field field) {
        Column column = field.getAnnotation(Column.class);
        return column.name();
    }

    private static String getFieldName(Field field) {
        return field.getName();
    }

    public ColumnMeta(Field field, String columnName) {
        this(
                field,
                ColumnType.getSqlType(field.getType()),
                columnName,
                getLength(field),
                getNullable(field),
                Relation.from(field)
        );
    }

    private static int getLength(Field field) {
        if (ColumnType.isNotVarcharType(field.getType())) {
            return 0;
        }

        if (isNotPresent(field, Column.class)) {
            return DEFAULT_LENGTH;
        }

        Column annotation = field.getAnnotation(Column.class);
        return annotation.length();
    }

    private static boolean getNullable(Field field) {
        if (isNotPresent(field, Column.class)) {
            return true;
        }
        Column annotation = field.getAnnotation(Column.class);
        return annotation.nullable();
    }

    public boolean notNull() {
        return !nullable;
    }

}
