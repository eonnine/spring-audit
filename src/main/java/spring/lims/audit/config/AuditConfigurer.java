package spring.lims.audit.config;

import spring.lims.audit.domain.DataBaseType;
import spring.lims.audit.domain.DisplayType;
import spring.lims.audit.domain.RecordScope;
import spring.lims.audit.domain.StringConvertCase;

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

    default DataBaseType databaseType() {
        return DataBaseType.ORACLE;
    }

}