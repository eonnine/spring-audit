package spring.audit.context;

import spring.audit.domain.AuditAttribute;
import spring.audit.domain.AuditTrail;
import spring.audit.event.AuditTransactionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAuditManager implements AuditManager {
    private final ThreadLocal<Map<String, List<AuditTrail>>> auditStore = new ThreadLocal<>();

    public void putAudits(List<AuditTrail> auditTrails) {
        if (auditStore.get() == null) {
            auditStore.set(new HashMap<>());
        }
        Map<String, List<AuditTrail>> resource = auditStore.get();
        resource.put(getCurrentTransactionId(), auditTrails);
    }

    public List<AuditTrail> getAudits() {
        String key = getCurrentTransactionId();
        if (auditStore.get() == null) {
            return null;
        }
        if (!auditStore.get().containsKey(key)) {
            return null;
        }
        return auditStore.get().get(getCurrentTransactionId());
    }

    @Override
    public void remove() {
        auditStore.remove();
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
    public abstract List<AuditAttribute> getAsList();
}