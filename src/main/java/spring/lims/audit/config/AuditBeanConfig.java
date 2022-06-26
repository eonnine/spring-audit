package spring.lims.audit.config;

import spring.lims.audit.context.AuditManager;
import spring.lims.audit.context.DecisiveRecordAuditManager;
import spring.lims.audit.context.FullRecordAuditManager;
import spring.lims.audit.domain.DataBaseType;
import spring.lims.audit.domain.RecordScope;
import spring.lims.audit.event.AuditEventListener;
import spring.lims.audit.event.AuditEventPublisher;
import spring.lims.audit.event.AuditTransactionListener;
import spring.lims.audit.sql.*;
import spring.lims.audit.util.AuditAnnotationReader;
import spring.lims.audit.util.FieldNameConverter;
import spring.lims.audit.util.StringConverter;

import javax.sql.DataSource;

public class AuditBeanConfig {

    private final AuditManager auditManager;
    private final AuditSqlRepository sqlRepository;
    private final AuditTransactionListener transactionListener;
    private final AuditAnnotationReader annotationReader;
    private final AuditSqlConfig sqlConfig;

    public AuditBeanConfig(DataSource dataSource, AuditConfigurer configurer, AuditEventListener eventListener) {
        StringConverter stringConverter = createStringConverter(configurer);
        AuditSqlManager sqlManager = new AuditSqlManager();
        AuditSqlGenerator sqlGenerator = createAuditSqlGenerator(configurer.databaseType());
        AuditSqlProvider sqlProvider = createAuditSqlProvider(sqlGenerator, stringConverter);
        AuditEventPublisher eventPublisher = createAuditEventPublisher(eventListener);

        this.auditManager = createAuditManager(configurer);
        this.sqlRepository = createAuditSqlRepository(dataSource, sqlManager, sqlProvider);
        this.annotationReader = new AuditAnnotationReader();
        this.transactionListener = createAuditTransactionListener(configurer, this.auditManager, eventPublisher);
        this.sqlConfig = createAuditSqlConfig(sqlManager, sqlProvider, this.annotationReader);

        postConstruct();
    }

    private void postConstruct() {
        this.sqlConfig.initialize();
    }

    public AuditManager auditManager() {
        return this.auditManager;
    }

    public AuditSqlRepository sqlRepository() {
        return this.sqlRepository;
    }

    public AuditTransactionListener transactionListener() {
        return this.transactionListener;
    }

    public AuditAnnotationReader annotationReader() {
        return this.annotationReader;
    }

    private AuditManager createAuditManager(AuditConfigurer configurer) {
        RecordScope scope = configurer.recordScope();
        if (scope.isEach()) {
            return new FullRecordAuditManager();
        }
        else if (scope.isTransaction()) {
            return new DecisiveRecordAuditManager();
        }
        throw new RuntimeException("Unknown auditTrail record scope. Configure record scope using AuditTrailConfigurer");
    }

    private AuditSqlGenerator createAuditSqlGenerator(DataBaseType dataBaseType) {
        if (dataBaseType.isOracle()) {
            return new OracleAuditSqlGenerator();
        }
        return new OracleAuditSqlGenerator();
    }

    private AuditSqlConfig createAuditSqlConfig(AuditSqlManager sqlManager, AuditSqlProvider sqlProvider, AuditAnnotationReader annotationReader) {
        return new AuditSqlConfig(sqlManager, sqlProvider, annotationReader);
    }

    private AuditSqlRepository createAuditSqlRepository(DataSource dataSource, AuditSqlManager sqlManager, AuditSqlProvider sqlProvider) {
        return new AuditSqlRepository(dataSource, sqlManager, sqlProvider);
    }

    private AuditSqlProvider createAuditSqlProvider(AuditSqlGenerator sqlGenerator, StringConverter stringConverter) {
        return new AuditSqlProvider(sqlGenerator, stringConverter);
    }

    private AuditEventPublisher createAuditEventPublisher(AuditEventListener eventListener) {
        return new AuditEventPublisher(eventListener);
    }

    private AuditTransactionListener createAuditTransactionListener(AuditConfigurer configurer, AuditManager auditManager, AuditEventPublisher eventPublisher) {
        return new AuditTransactionListener(configurer, auditManager, eventPublisher);
    }

    private StringConverter createStringConverter(AuditConfigurer configurer) {
        return new FieldNameConverter(configurer);
    }


}
