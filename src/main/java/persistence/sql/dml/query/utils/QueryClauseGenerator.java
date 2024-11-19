package persistence.sql.dml.query.utils;

import java.util.List;
import java.util.stream.Collectors;
import persistence.sql.dml.query.WhereCondition;

public class QueryClauseGenerator {

    private static final String WHERE = "where";
    private static final String AND = "and";


    public static String whereClause(List<WhereCondition> whereConditions) {
        return new StringBuilder()
                .append( " " )
                .append( WHERE )
                .append(
                        whereConditions.stream()
                        .map(condition -> condition.name() + " " + condition.operator().value() + " " + condition.value())
                        .collect(Collectors.joining(AND, " ", ""))
                ).toString();
    }

}
