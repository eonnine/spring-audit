package spring.audit.domain;

import spring.audit.type.CommandType;

import java.util.Map;

public class AuditTrail {

    private final CommandType commandType;
    private final String label;
    private final String content;
    private final String title;
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

    public String getTitle() {
        return this.title;
    }

    public Map<String, Object> getId() {
        return this.id;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    AuditTrail(CommandType commandType, String label, String content, String title, Map<String, Object> id, Map<String, Object> param) {
        this.commandType = commandType;
        this.label = label;
        this.content = content;
        this.title = title;
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
        private String title;
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

        public AuditTrailBuilder title(String title) {
            this.title = title;
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
            return new AuditTrail(this.commandType, this.label, this.content, this.title, this.id, this.param);
        }
    }
}