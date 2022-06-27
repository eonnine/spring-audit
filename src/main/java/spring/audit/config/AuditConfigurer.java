package spring.audit.config;

import spring.audit.domain.DatabaseType;
import spring.audit.domain.DisplayType;
import spring.audit.domain.RecordScope;
import spring.audit.domain.StringConvertCase;

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