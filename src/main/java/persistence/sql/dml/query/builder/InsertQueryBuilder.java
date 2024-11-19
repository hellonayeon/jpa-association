package persistence.sql.dml.query.builder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import persistence.meta.ColumnMeta;
import persistence.meta.TableMeta;
import persistence.sql.ddl.type.ColumnType;

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

    public InsertQueryBuilder insert(TableMeta tableMeta, List<ColumnMeta> columnMetas) {
        return insert(
                tableMeta.name(),
                columnMetas.stream().map(ColumnMeta::name).toList(),
                Collections.emptyList()
        );
    }

    public InsertQueryBuilder insert(String tableName, List<String> columnNames) {
        return insert(tableName, columnNames, Collections.emptyList());
    }

    public InsertQueryBuilder insert(String tableName, List<String> columnNames, List<String> joinColumnNames) {
        queryString.append( INSERT_INTO )
                .append( " " )
                .append( tableName )
                .append( columnClause( Stream.concat(columnNames.stream(), joinColumnNames.stream()).toList() ));
        return this;
    }

    public InsertQueryBuilder values(List<Object> values) {
        return values(values, Collections.emptyList());
    }

    public InsertQueryBuilder values(List<Object> values, List<Object> joinColumnValues) {
        queryString.append( " " )
                .append( VALUES )
                .append( valueClause( Stream.concat(values.stream(), joinColumnValues.stream()).toList()) );
        return this;
    }

    private String columnClause(List<String> columnNames) {
        return new StringBuilder()
                .append( " (" )
                .append( String.join(", ", columnNames) )
                .append( ")" )
                .toString();
    }

    private String valueClause(List<Object> values) {
        return new StringBuilder()
                .append(" (")
                .append( values.stream()
                        .map(this::format)
                        .collect(Collectors.joining(", ")) )
                .append(")")
                .toString();
    }

    public String format(Object value) {
        if (value == null) {
            return null;
        }

        if (ColumnType.isVarcharType(value.getClass())) {
            return "'" + value + "'";
        }
        return value.toString();
    }

}
