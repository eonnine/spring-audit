package spring.audit.sql;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import spring.audit.domain.SqlColumn;
import spring.audit.domain.SqlEntity;
import spring.audit.domain.SqlParameter;
import spring.audit.domain.SqlRow;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import spring.audit.type.CommandType;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AuditSqlRepository {

    private final DataSource dataSource;
    private final AuditSqlManager sqlManager;
    private final AuditSqlProvider sqlProvider;

    public AuditSqlRepository(DataSource dataSource, AuditSqlManager sqlManager, AuditSqlProvider sqlProvider) {
        this.dataSource = dataSource;
        this.sqlManager = sqlManager;
        this.sqlProvider = sqlProvider;
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    public List<SqlRow> findAllById(String sqlManagerKey, Map<String, Object> parameter) {
        List<SqlParameter> sqlParameters = getSqlParameters(sqlManagerKey, parameter);
        String commentSuffix = AbstractSqlGenerator.COMMENT_SUFFIX;
        SqlEntity sqlEntity = sqlManager.get(sqlManagerKey);
        String conditionClause = sqlProvider.generateConditionClause(sqlParameters);

        String sql = sqlEntity.getSelectClause() + conditionClause;

        List<SqlRow> result = new ArrayList<>();
        try {
            PreparedStatement statement = getConnection().prepareStatement(sql);

            for (int i = 0; i < sqlParameters.size(); i++) {
                statement.setString(i + 1, String.valueOf(sqlParameters.get(i).getData()));
            }

            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String columnLabel = null;

            while (resultSet.next()) {
                SqlRow row = new SqlRow();
                for (int i = 1; i <= columnCount; i++) {
                    columnLabel = metaData.getColumnLabel(i);

                    if (columnLabel.contains(commentSuffix)) {
                        continue;
                    }

                    SqlColumn column = new SqlColumn();
                    column.setData(resultSet.getString(columnLabel));
                    column.setComment(resultSet.getString(columnLabel + commentSuffix));

                    row.put(metaData.getColumnName(i), column);
                }
                result.add(row);
            }
        } catch(SQLException e) {
            throw new InvalidDataAccessResourceUsageException(e.getMessage(), e.getCause());
        }
        return result;
    }

    public List<SqlParameter> getSqlParameters(String sqlManagerKey, Map<String, Object> parameter) {
        assertExistsParameter(parameter);
        SqlEntity sqlEntity = sqlManager.get(sqlManagerKey);
        return getParameterIdFields(sqlEntity.getIdFields(), parameter).stream()
                .map(fieldName -> {
                    SqlParameter sqlParameter = new SqlParameter();
                    sqlParameter.setName(sqlProvider.convertCase(fieldName));
                    sqlParameter.setData(parameter.get(fieldName));
                    return sqlParameter;
                })
                .collect(Collectors.toList());
    }

    private List<String> getParameterIdFields(List<String> idFields, Map<String, Object> parameter) {
        if (isEqualsParameter(idFields, parameter)) {
            return idFields;
        }
        List<String> sameOrLessIdFields = idFields.stream().filter(parameter::containsKey).collect(Collectors.toList());
        if (sameOrLessIdFields.size() == 0) {
            throw new IllegalArgumentException("Parameter does not have id fields. not exists fields -> [" + String.join(", ", idFields) + "]");
        }
        return sameOrLessIdFields;
    }

    public boolean isEqualsParameter(List<String> idFieldNames, Map<String, Object> parameter) {
        return idFieldNames.size() == idFieldNames.stream().filter(parameter::containsKey).count();
    }

    private void assertExistsParameter(Map<String, Object> parameter) {
        if (parameter.isEmpty()) {
            throw new IllegalArgumentException("Not found sql parameter. Parameter is required. [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
    }

}