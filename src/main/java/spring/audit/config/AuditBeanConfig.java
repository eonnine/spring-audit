package spring.audit.config;

import org.springframework.context.ApplicationContext;
import spring.audit.context.AuditApplicationContextAware;
import spring.audit.context.AuditManager;
import spring.audit.context.DecisiveRecordAuditManager;
import spring.audit.context.FullRecordAuditManager;
import spring.audit.domain.DatabaseType;
import spring.audit.domain.RecordScope;
import spring.audit.event.AuditEventListener;
import spring.audit.event.AuditEventPublisher;
import spring.audit.event.AuditTransactionListener;
import spring.audit.event.DefaultAuditEventListener;
import spring.audit.sql.*;
import spring.audit.util.AuditAnnotationReader;
import spring.audit.util.FieldNameConverter;
import spring.audit.util.StringConverter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class AuditBeanConfig {

    private final DataSource dataSource;
    private final Map<String, Object> beanFactory = new HashMap<>();

    public AuditBeanConfig(AuditApplicationContextAware contextAware, DataSource dataSource) {
        this.dataSource = dataSource;
        setBean(AuditApplicationContextAware.class, contextAware);
        postConstruct();
    }

    private void postConstruct() {
        sqlConfig().initialize();
    }

    private void setBean(Class<?> clazz, Object instance) {
        beanFactory.put(clazz.getName(), instance);
    }

    private ApplicationContext applicationContext() {
        return ((AuditApplicationContextAware) beanFactory.get(AuditApplicationContextAware.class.getName())).getApplicationContext();
    }

    private boolean existsBean(Class<?> clazz) {
        return !applicationContext().getBeansOfType(clazz).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private <T> T getBean(Class<T> clazz, T instance) {
        String name = clazz.getName();
        if (!beanFactory.containsKey(name)) {
            beanFactory.put(name, instance);
        }
        return (T) beanFactory.get(name);
    }

    /**
     * context
     */
    public AuditManager auditManager() {
        RecordScope scope = configurer().recordScope();
        if (scope.isEach()) {
            return getBean(AuditManager.class, new FullRecordAuditManager());
        }
        else if (scope.isTransaction()) {
            return getBean(AuditManager.class, new DecisiveRecordAuditManager());
        }
        throw new RuntimeException("Unknown auditTrail record scope. Configure record scope using AuditTrailConfigurer");
    }

    private AuditConfigurer configurer() {
        if (existsBean(AuditConfigurer.class)) {
            return applicationContext().getBean(AuditConfigurer.class);
        }
        return getBean(AuditConfigurer.class, new DefaultAuditConfigurer());
    }


    /**
     * sql
     */
    public AuditSqlRepository sqlRepository() {
        return getBean(AuditSqlRepository.class, new AuditSqlRepository(dataSource, sqlManager(), sqlProvider()));
    }

    private AuditSqlManager sqlManager() {
        return getBean(AuditSqlManager.class, new AuditSqlManager());
    }

    private AuditSqlProvider sqlProvider() {
        return getBean(AuditSqlProvider.class, new AuditSqlProvider(sqlGenerator(), stringConverter()));
    }

    private AuditSqlGenerator sqlGenerator() {
        DatabaseType type = configurer().databaseType();
        if (type.isOracle()) {
            return getBean(AuditSqlGenerator.class, new OracleAuditSqlGenerator());
        }
        throw new RuntimeException("Unknown auditTrail database type. Configure database type using AuditTrailConfigurer");
    }

    private AuditSqlConfig sqlConfig() {
        return getBean(AuditSqlConfig.class, new AuditSqlConfig(sqlManager(), sqlProvider(), annotationReader()));
    }


    /**
     * event
     */
    public AuditTransactionListener transactionListener() {
        return getBean(AuditTransactionListener.class, new AuditTransactionListener(configurer(), auditManager(), eventPublisher()));
    }

    private AuditEventListener eventListener() {
        if (existsBean(AuditEventListener.class)) {
            return applicationContext().getBean(AuditEventListener.class);
        }
        return getBean(AuditEventListener.class, new DefaultAuditEventListener());
    }

    private AuditEventPublisher eventPublisher() {
        return getBean(AuditEventPublisher.class, new AuditEventPublisher(eventListener()));
    }


    /**
     * util
     */
    public AuditAnnotationReader annotationReader() {
        return getBean(AuditAnnotationReader.class, new AuditAnnotationReader());
    }

    private StringConverter stringConverter() {
        return getBean(StringConverter.class, new FieldNameConverter(configurer()));
    }

}