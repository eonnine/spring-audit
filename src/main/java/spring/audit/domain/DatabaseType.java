package spring.audit.domain;

public enum DatabaseType {
    ORACLE;

    public boolean isOracle() {
        return this == DatabaseType.ORACLE;
    }
}