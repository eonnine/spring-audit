package spring.lims.audit.event;

import spring.lims.audit.domain.AuditTrail;

import java.util.List;

public interface AuditEventListener {

    default void beforeCommit(List<AuditTrail> auditTrail) {};

    default void afterCommit(List<AuditTrail> auditTrail) {};

}