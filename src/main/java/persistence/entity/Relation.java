package persistence.entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import persistence.exception.NotExistException;
import persistence.exception.UnknownException;
import persistence.sql.metadata.TableName;

public class Relation {

    private final TableName joinTableName;
    private final String joinColumnName;
    private final FetchType fetchType;
    private final boolean hasRelation;

    private Relation() {
        this.joinTableName = null;
        this.joinColumnName = null;
        this.fetchType = null;
        this.hasRelation = false;
    }

    private Relation(TableName joinTableName, String joinColumnName, FetchType fetchType) {
        this.joinTableName = joinTableName;
        this.joinColumnName = joinColumnName;
        this.fetchType = fetchType;
        this.hasRelation = true;
    }


    public static Relation from(Field field) {
        if (field.isAnnotationPresent(OneToMany.class)) {
            return oneToManyRelation(field);
        }

        return new Relation();
    }

    private static Relation oneToManyRelation(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType parameterizedType) {
            Type type = parameterizedType.getActualTypeArguments()[0];
            OneToMany oneToMany = field.getAnnotation(OneToMany.class);
            return new Relation(
                    new TableName((Class<?>) type),
                    joinColumnName(field),
                    oneToMany.fetch()
            );
        }
        throw new UnknownException("ParameterizedType type: " + genericType.getTypeName());
    }

    private static String joinColumnName(Field field) {
        if (field.isAnnotationPresent(JoinColumn.class)) {
            JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
            return joinColumn.name();
        }
        throw new NotExistException("@JoinColumn annotation. field: " + field.getName());
    }

}
