package persistence.sql.ddl.query.builder;

import static persistence.sql.ddl.query.builder.ColumnDefinition.define;
import static persistence.sql.ddl.query.builder.TableDefinition.definePrimaryKeyColumn;
import static persistence.sql.ddl.query.builder.TableDefinition.definePrimaryKeyConstraint;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.ddl.query.ColumnMeta;
import persistence.sql.dialect.Dialect;
import persistence.sql.metadata.PrimaryKeyConstraint;
import persistence.sql.metadata.TableName;

public class CreateQueryBuilder {

    private static final String CREATE_TABLE = "create table";

    private final Dialect dialect;
    private final StringBuilder queryString;

    private CreateQueryBuilder(Dialect dialect) {
        this.dialect = dialect;
        this.queryString = new StringBuilder();
    }

    public static CreateQueryBuilder builder(Dialect dialect) {
        return new CreateQueryBuilder(dialect);
    }

    public String build() {
        return queryString.toString();
    }

    public CreateQueryBuilder create(TableName tableName, PrimaryKeyConstraint primaryKeyConstraint, List<ColumnMeta> columns) {
        queryString.append( CREATE_TABLE )
                .append( " " )
                .append( tableName.value() )
                .append( " (" );

        queryString.append( definePrimaryKeyColumn(primaryKeyConstraint, dialect) ).append(", ");
        queryString.append(
                columns.stream()
                .map(column -> define(column, dialect))
                .collect(Collectors.joining(", "))
        );
        queryString.append( definePrimaryKeyConstraint(primaryKeyConstraint) );

        queryString.append(")");
        return this;
    }



}
