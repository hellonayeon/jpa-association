package persistence.query;

import jdbc.JdbcTemplate;
import persistence.sql.ddl.query.builder.CreateQueryBuilder;
import persistence.sql.ddl.query.builder.DropQueryBuilder;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.query.InsertQuery;
import persistence.sql.dml.query.builder.InsertQueryBuilder;

public class QueryExecutor {

    public static void create(Class<?> clazz, JdbcTemplate jdbcTemplate) {
        String query = CreateQueryBuilder.builder(new H2Dialect())
                .create(clazz)
                .build();
        jdbcTemplate.execute(query);
    }

    public static void drop(Class<?> clazz, JdbcTemplate jdbcTemplate) {
        String query = DropQueryBuilder.builder(new H2Dialect())
                .drop(clazz)
                .build();
        jdbcTemplate.execute(query);
    }

    public static void insert(Object object, JdbcTemplate jdbcTemplate) {
        InsertQuery insertQuery = new InsertQuery(object);
        InsertQueryBuilder insertQueryBuilder = InsertQueryBuilder.builder()
                .insert(insertQuery.tableName(), insertQuery.columns())
                .values(insertQuery.columns());
        jdbcTemplate.execute(insertQueryBuilder.build());
    }


}
