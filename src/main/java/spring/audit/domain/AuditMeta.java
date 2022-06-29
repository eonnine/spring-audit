package spring.audit.domain;

import spring.audit.annotation.Audit;

import java.util.List;
import java.util.Map;

public class AuditMeta {

    private Audit auditAnnotation;

    private Class<?> entity;
    private Map<String, Object> entityId;

    private List<SqlParameter> sqlParams;

    private String auditManagerKey;
    private String sqlManagerKey;

    public Audit getAuditAnnotation() {
        return auditAnnotation;
    }

    public void setAuditAnnotation(Audit auditAnnotation) {
        this.auditAnnotation = auditAnnotation;
    }

    public Class<?> getEntity() {
        return entity;
    }

    public void setEntity(Class<?> entity) {
        this.entity = entity;
    }

    public Map<String, Object> getEntityId() {
        return entityId;
    }

    public void setEntityId(Map<String, Object> entityId) {
        this.entityId = entityId;
    }

    public List<SqlParameter> getSqlParams() {
        return sqlParams;
    }

    public void setSqlParams(List<SqlParameter> sqlParams) {
        this.sqlParams = sqlParams;
    }

    public String getAuditManagerKey() {
        return auditManagerKey;
    }

    public void setAuditManagerKey(String auditManagerKey) {
        this.auditManagerKey = auditManagerKey;
    }

    public String getSqlManagerKey() {
        return sqlManagerKey;
    }

    public void setSqlManagerKey(String sqlManagerKey) {
        this.sqlManagerKey = sqlManagerKey;
    }
}