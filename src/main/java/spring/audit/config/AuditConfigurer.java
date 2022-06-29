package spring.audit.config;

import spring.audit.type.DatabaseType;
import spring.audit.type.DisplayType;
import spring.audit.type.RecordScope;
import spring.audit.type.StringConvertCase;

public interface AuditConfigurer {

    default RecordScope recordScope() {
        return RecordScope.TRANSACTION;
    }

    default DisplayType displayType() {
        return DisplayType.COMMENT;
    }

    default StringConvertCase convertCase() {
        return StringConvertCase.CAMEL_TO_SNAKE;
    }

    default DatabaseType databaseType() {
        return DatabaseType.ORACLE;
    }

}