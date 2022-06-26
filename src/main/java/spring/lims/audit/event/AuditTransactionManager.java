package spring.lims.audit.event;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

public class AuditTransactionManager {

    private static final String RESOURCE_NAME = "auditTailIdentifier";
               
    private AuditTransactionManager() {}

    private static String createTransactionId() {
        return UUID.randomUUID().toString();
    }

    public static void initCurrentTransaction() {
        if (hasCurrentTransactionResource()) {
            throw new RuntimeException("Already exists auditTrail resource in current transaction [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.initSynchronization();
        }
        setCurrentTransactionResource(createTransactionId());
    }

    public static String getCurrentTransactionId() {
        if (!hasCurrentTransactionResource()) {
            throw new RuntimeException("Not found current auditTrail resource in current transaction [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
        }
        return (String) TransactionSynchronizationManager.getResource(RESOURCE_NAME);
    }

    public static void removeCurrentTransactionId() {
        if (hasCurrentTransactionResource()) {
            TransactionSynchronizationManager.unbindResource(RESOURCE_NAME);
        }
    }

    public static boolean isCurrentTransactionActive() {
        return hasCurrentTransactionResource();
    }

    public static void bindListener(AuditTransactionListener listener) {
        TransactionSynchronizationManager.registerSynchronization(listener);
    }

    private static boolean hasCurrentTransactionResource() {
        return TransactionSynchronizationManager.hasResource(RESOURCE_NAME);
    }

    private static void setCurrentTransactionResource(String transactionId) {
        TransactionSynchronizationManager.bindResource(RESOURCE_NAME, transactionId);
    }

}