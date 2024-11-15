package persistence.sql.ddl.query;

import java.lang.reflect.Field;

public interface Association {

    ForeignKeyConstraint foreignKeyConstraint(Class<?> clazz, Field field);

}
