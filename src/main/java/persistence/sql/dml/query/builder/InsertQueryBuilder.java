package persistence.sql.dml.query.builder;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.dml.query.ColumnNameValue;
import persistence.sql.metadata.ColumnName;
import persistence.sql.metadata.TableName;
import persistence.sql.dml.query.utils.QueryClauseGenerator;

public class InsertQueryBuilder {

    private static final String INSERT_INTO = "insert into";
    private static final String VALUES = "values";

    private final StringBuilder queryString;

    private InsertQueryBuilder() {
        this.queryString = new StringBuilder();
    }

    public static InsertQueryBuilder builder() {
        return new InsertQueryBuilder();
    }

    public String build() {
        return queryString.toString();
    }

    public InsertQueryBuilder insert(TableName tableName, List<ColumnNameValue> columns) {
        queryString.append( INSERT_INTO )
                .append( " " )
                .append( tableName.value() )
                .append( columnClause(columns.stream().map(ColumnNameValue::columnName).toList()) );
        return this;
    }

    public InsertQueryBuilder values(List<ColumnNameValue> columns) {
        queryString.append( " " )
                .append( VALUES )
                .append( valueClause(columns) );
        return this;
    }

    private String columnClause(List<ColumnName> columnNames) {
        return new StringBuilder()
                .append( " (" )
                .append( QueryClauseGenerator.columnClause(columnNames))
                .append( ")" )
                .toString();
    }

    private String valueClause(List<ColumnNameValue> columns) {
        return new StringBuilder()
                .append(" (")
                .append( columns.stream()
                        .map(ColumnNameValue::columnValueString)
                        .collect(Collectors.joining(", ")) )
                .append(")")
                .toString();
    }

}
