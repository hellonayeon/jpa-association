package persistence.sql.ddl.query;

public interface ForeignKeyConstraint {

    Class<?> appliedClass();

    String constraint();

}
