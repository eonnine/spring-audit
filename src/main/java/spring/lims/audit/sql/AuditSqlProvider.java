package spring.lims.audit.sql;

import spring.lims.audit.domain.SqlEntity;
import spring.lims.audit.domain.SqlParameter;
import spring.lims.audit.util.StringConverter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public String makeConditionClause(List<SqlParameter> sqlParameters) {
        return " WHERE " + sqlParameters.stream()
                .map(p -> p.getName() + " = ?")
                .collect(Collectors.joining(" AND "));
    }

    public List<SqlParameter> getSqlParameters(SqlEntity entity, Map<String, Object> parameter) {
        assertExistsParameter(parameter);
        return getParameterIdFields(entity.getIdFields(), parameter).stream()
                .map(fieldName -> {
                    SqlParameter sqlParameter = new SqlParameter();
                    sqlParameter.setName(converter.convert(fieldName));
                    sqlParameter.setData(parameter.get(fieldName));
                    return sqlParameter;
                })
                .collect(Collectors.toList());
    }

    private List<String> getParameterIdFields(List<String> idFields, Map<String, Object> parameter) {
        List<String> sameOrLessIdFields = idFields.stream().filter(parameter::containsKey).collect(Collectors.toList());
        if (sameOrLessIdFields.size() == 0) {
            throw new RuntimeException("Parameter does not have id fields. not exists fields -> [" + String.join(", ", idFields) + "]");
        }
        return idFields.size() == sameOrLessIdFields.size() ? idFields : sameOrLessIdFields;
    }

    private List<String> makeColumnNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> converter.convert(field.getName()))
                .collect(Collectors.toList());
    }

    private void assertExistsParameter(Map<String, Object> parameter) {
        if (parameter.isEmpty()) {
            throw new IllegalArgumentException("Not found sql parameter. Parameter is required. [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
    }
}