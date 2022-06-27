package spring.audit.context;

import spring.audit.domain.AuditAttribute;
import spring.audit.domain.AuditTrail;
import spring.audit.event.AuditTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAuditManager implements AuditManager {
    private final Map<String, List<AuditTrail>> auditStore = new HashMap<>();

    public void putAudits(List<AuditTrail> auditTrails) {
        auditStore.put(getCurrentTransactionId(), auditTrails);
    }

    public List<AuditTrail> getAudits() {
        return auditStore.get(getCurrentTransactionId());
    }

    protected String getCurrentTransactionId() {
        return AuditTransactionManager.getCurrentTransactionId();
    }

    @Override
    public abstract void put(String key, AuditAttribute auditTrail);

    @Override
    public abstract AuditAttribute get(String key);

    @Override
    public abstract boolean has(String key);

    @Override
    public abstract void remove();

    @Override
    public abstract List<AuditAttribute> getAsList();
}