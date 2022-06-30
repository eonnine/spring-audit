package spring.audit.domain;

import spring.audit.annotation.Audit;

public class AuditMeta {
    private Class<?> entityClazz;
    private Audit auditAnnotation;
    private String auditManagerKey;
    private String sqlManagerKey;

    public Audit getAuditAnnotation() {
        return auditAnnotation;
    }

    public void setAuditAnnotation(Audit auditAnnotation) {
        this.auditAnnotation = auditAnnotation;
    }

    public Class<?> getEntityClazz() {
        return entityClazz;
    }

    public void setEntityClazz(Class<?> entityClazz) {
        this.entityClazz = entityClazz;
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