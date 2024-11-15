package persistence.sql.ddl.query.constraint;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.lang.reflect.Field;
import java.util.Arrays;
import persistence.exception.NotExistException;
import persistence.sql.ddl.query.ColumnMeta;

public record PrimaryKeyConstraint(ColumnMeta column,
                                   GenerationType generationType) {

    public static PrimaryKeyConstraint from(Field[] fields) {
        Field identifierField = Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new NotExistException("identification."));
        return new PrimaryKeyConstraint(new ColumnMeta(identifierField), generationType(identifierField));
    }

    private static GenerationType generationType(Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            GeneratedValue annotation = field.getAnnotation(GeneratedValue.class);
            return annotation.strategy();
        }
        return GenerationType.IDENTITY;
    }

}
