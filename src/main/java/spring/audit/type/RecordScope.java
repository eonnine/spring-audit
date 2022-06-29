package spring.audit.type;

public enum RecordScope {
    EACH,
    TRANSACTION;

    public boolean isEach() {
        return this == RecordScope.EACH;
    }

    public boolean isTransaction() {
        return this == RecordScope.TRANSACTION;
    }
}