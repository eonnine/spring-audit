package spring.audit.sql;

import spring.audit.domain.SqlParameter;
import spring.audit.util.StringConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AuditSqlProvider {

    private final AuditSqlGenerator generator;
    private final StringConverter converter;

    public AuditSqlProvider(AuditSqlGenerator generator, StringConverter converter) {
        this.generator = generator;
        this.converter = converter;
    }

    public String generateSelectClause(Class<?> clazz, String tableName) {
        List<String> columnNames = makeColumnNames(clazz);
        return generator.makeSelectSqlWithComment(columnNames, tableName);
    }

    public String generateConditionClause(List<SqlParameter> sqlParameters) {
        return " WHERE " + sqlParameters.stream()
                .map(p -> p.getName() + " = ?")
                .collect(Collectors.joining(" AND "));
    }

    public String convertCase(String s) {
        return converter.convert(s);
    }

    private List<String> makeColumnNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> converter.convert(field.getName()))
                .collect(Collectors.toList());
    }

}