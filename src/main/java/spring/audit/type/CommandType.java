package spring.audit.type;

public enum CommandType {
    INSERT,
    UPDATE,
    DELETE;

    public boolean isInsert() {
        return this == CommandType.INSERT;
    }
}