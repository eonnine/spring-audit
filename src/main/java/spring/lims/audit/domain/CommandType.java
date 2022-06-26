package spring.lims.audit.domain;

public enum CommandType {
    INSERT,
    UPDATE,
    DELETE;

    public boolean isInsert() {
        return this == CommandType.INSERT;
    }
}
