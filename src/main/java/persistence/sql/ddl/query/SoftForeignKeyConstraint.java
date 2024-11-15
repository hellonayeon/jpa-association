package persistence.sql.ddl.query;

import java.lang.reflect.Field;

public class SoftForeignKeyConstraint implements ForeignKeyConstraint {

    private final ColumnMeta columnMeta;

    public SoftForeignKeyConstraint(Field field, String columnName) {
        this.columnMeta = new ColumnMeta(field, columnName);
    }

}
