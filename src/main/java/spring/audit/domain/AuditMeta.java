package spring.audit.domain;

import spring.audit.annotation.Audit;
import spring.audit.annotation.AuditEntity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class AuditMeta {

    private Method method;
    private Object[] args;

    private Audit auditAnnotation;
    private AuditEntity auditEntityAnnotation;

    private Class<?> entity;
    private Map<String, Object> entityId;

    private Map<String, Object> originParam;
    private List<SqlParameter> sqlParams;

    private String auditManagerKey;
    private String sqlManagerKey;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Audit getAuditAnnotation() {
        return auditAnnotation;
    }

    public void setAuditAnnotation(Audit auditAnnotation) {
        this.auditAnnotation = auditAnnotation;
    }

    public AuditEntity getAuditEntityAnnotation() {
        return auditEntityAnnotation;
    }

    public void setAuditEntityAnnotation(AuditEntity auditEntityAnnotation) {
        this.auditEntityAnnotation = auditEntityAnnotation;
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

    public Map<String, Object> getOriginParam() {
        return originParam;
    }

    public void setOriginParam(Map<String, Object> originParam) {
        this.originParam = originParam;
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