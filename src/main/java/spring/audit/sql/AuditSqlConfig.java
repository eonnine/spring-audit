package spring.audit.sql;

import org.springframework.core.annotation.AnnotationConfigurationException;
import spring.audit.annotation.AuditEntity;
import spring.audit.domain.SqlEntity;
import spring.audit.sql.AuditSqlManager;
import spring.audit.sql.AuditSqlProvider;
import spring.audit.util.AuditAnnotationReader;
import org.springframework.beans.factory.config.BeanDefinition;

import javax.management.InstanceAlreadyExistsException;
import javax.naming.NameAlreadyBoundException;
import java.rmi.AlreadyBoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AuditSqlConfig {

    private final AuditSqlManager sqlManager;
    private final AuditSqlProvider sqlProvider;
    private final AuditAnnotationReader annotationReader;

    public AuditSqlConfig(AuditSqlManager sqlManager, AuditSqlProvider sqlProvider, AuditAnnotationReader annotationReader) {
        this.sqlManager = sqlManager;
        this.sqlProvider = sqlProvider;
        this.annotationReader = annotationReader;
    }

    public void initialize() {
        try {
            Map<String, Object> entities = annotationReader.getAnnotationBeans(AuditEntity.class);
            Collection<Object> beans = entities.values();

            for (Object bean : beans) {
                String className = bean.getClass().getName();

                Class<?> entityClazz = Class.forName(className);
                AuditEntity entityAnnotation = entityClazz.getAnnotation(AuditEntity.class);
                List<String> idFields = annotationReader.getIdFieldNames(entityClazz);
                String tableName = entityAnnotation.name();

                if (sqlManager.has(tableName)) {
                    throw new AnnotationConfigurationException("There is already entity name of 'AuditEntity' annotation. '" + tableName + "' [" + className + "]");
                }

                String selectClause = sqlProvider.generateSelectClause(entityClazz, tableName);

                SqlEntity entity = new SqlEntity();
                entity.setName(className);
                entity.setTarget(entityClazz);
                entity.setIdFields(idFields);
                entity.setSelectClause(selectClause);

                sqlManager.put(tableName, entity);
            }
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("Not found AuditEntity. " + e.getMessage());
        }
    }

}