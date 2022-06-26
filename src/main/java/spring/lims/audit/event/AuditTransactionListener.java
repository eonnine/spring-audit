package spring.lims.audit.event;

import spring.lims.audit.config.AuditConfigurer;
import spring.lims.audit.context.AuditManager;
import spring.lims.audit.domain.AuditAttribute;
import spring.lims.audit.domain.AuditTrail;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;
import java.util.stream.Collectors;

public class AuditTransactionListener implements TransactionSynchronization {

    private final AuditConfigurer configurer;
    private final AuditManager auditManager;
    private final AuditEventPublisher eventPublisher;

    public AuditTransactionListener(AuditConfigurer configurer, AuditManager auditManager, AuditEventPublisher eventPublisher) {
        this.configurer = configurer;
        this.auditManager = auditManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            return;
        }
        List<AuditTrail> audits = convertToAudit(auditManager.getAsList());
        auditManager.putAudits(audits);
        eventPublisher.publishBeforeCommit(audits);
    }

    @Override
    public void afterCommit() {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            return;
        }
        eventPublisher.publishAfterCommit(auditManager.getAudits());
    }

    @Override
    public void afterCompletion(int status) {
        clear();
    }

    private void clear() {
        auditManager.remove();
        AuditTransactionManager.removeCurrentTransactionId();
    }

    private List<AuditTrail> convertToAudit(List<AuditAttribute> attributes) {
        return attributes.stream()
                .filter(AuditAttribute::isUpdated)
                .map(attribute -> attribute.toAuditTail(configurer))
                .collect(Collectors.toList());
    }

}