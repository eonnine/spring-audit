package spring.lims.audit.domain;

import java.util.Map;

public class AuditTrail {

    private final CommandType commandType;
    private final String label;
    private final String content;
    private final String diff;
    private final Map<String, Object> id;
    private final Map<String, Object> param;

    public CommandType getCommandType() {
        return commandType;
    }

    public String getLabel() {
        return this.label;
    }

    public String getContent() {
        return this.content;
    }

    public String getDiff() {
        return this.diff;
    }

    public Map<String, Object> getId() {
        return this.id;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    AuditTrail(CommandType commandType, String label, String content, String diff, Map<String, Object> id, Map<String, Object> param) {
        this.commandType = commandType;
        this.label = label;
        this.content = content;
        this.diff = diff;
        this.id = id;
        this.param = param;
    }

    public static AuditTrailBuilder builder() {
        return new AuditTrailBuilder();
    }

    public static class AuditTrailBuilder {
        private CommandType commandType;
        private String label;
        private String content;
        private String diff;
        private Map<String, Object> id;
        private Map<String, Object> param;

        AuditTrailBuilder() {
        }

        public AuditTrailBuilder commandType(CommandType commandType) {
            this.commandType = commandType;
            return this;
        }

        public AuditTrailBuilder label(String label) {
            this.label = label;
            return this;
        }

        public AuditTrailBuilder content(String content) {
            this.content = content;
            return this;
        }

        public AuditTrailBuilder diff(String diff) {
            this.diff = diff;
            return this;
        }

        public AuditTrailBuilder id(Map<String, Object> id) {
            this.id = id;
            return this;
        }

        public AuditTrailBuilder param(Map<String, Object> param) {
            this.param = param;
            return this;
        }

        public AuditTrail build() {
            return new AuditTrail(this.commandType, this.label, this.content, this.diff, this.id, this.param);
        }
    }
}