package spring.audit.event;

import spring.audit.domain.AuditTrail;

import java.util.List;

public interface AuditEventListener {

    default void beforeCommit(List<AuditTrail> auditTrail) {};

    default void afterCommit(List<AuditTrail> auditTrail) {};

}