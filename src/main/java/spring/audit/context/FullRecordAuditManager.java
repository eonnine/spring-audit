package spring.audit.context;

import spring.audit.domain.AuditAttribute;

import java.util.*;
import java.util.stream.Collectors;

public class FullRecordAuditManager extends AbstractAuditManager {
    private static final ThreadLocal<LinkedHashMap<String, List<AuditAttribute>>> attributeStore = new ThreadLocal<>();

    @Override
    public void put(String key, AuditAttribute auditAttribute) {
        if (attributeStore.get() == null) {
            attributeStore.set(new LinkedHashMap<>());
        }

        Map<String, List<AuditAttribute>> resource = attributeStore.get();
        if (!resource.containsKey(key)) {
            resource.put(key, new ArrayList<>());
        }

        List<AuditAttribute> auditAttributes = resource.get(key);
        auditAttributes.add(auditAttribute);
    }

    @Override
    public AuditAttribute get(String key) {
        Map<String, List<AuditAttribute>> resource = attributeStore.get();
        if (!resource.containsKey(key)) {
            return null;
        }

        List<AuditAttribute> auditTrails = resource.get(key);
        if (auditTrails.size() == 0) {
            return null;
        }
        return auditTrails.get(auditTrails.size() - 1);
    }

    @Override
    public boolean has(String key) {
        return false;
    }

    @Override
    public List<AuditAttribute> getAsList() {
        return attributeStore.get()
                .values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public void remove() {
        attributeStore.remove();
    }

}