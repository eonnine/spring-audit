package spring.audit.type;

public enum DatabaseType {
    ORACLE;

    public boolean isOracle() {
        return this == DatabaseType.ORACLE;
    }
}