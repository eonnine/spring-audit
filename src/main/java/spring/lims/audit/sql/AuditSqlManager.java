package spring.lims.audit.sql;

import spring.lims.audit.domain.SqlEntity;

import java.util.HashMap;
import java.util.Map;

public class AuditSqlManager {

    private final Map<String, SqlEntity> sql = new HashMap<>();

    public void put(String name, SqlEntity entity) {
        sql.put(name, entity);
    }

    public SqlEntity get(String name) {
        return sql.get(name);
    }
}