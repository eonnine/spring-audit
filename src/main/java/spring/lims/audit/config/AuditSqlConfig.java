package spring.lims.audit.config;

import spring.lims.audit.annotation.AuditEntity;
import spring.lims.audit.domain.SqlEntity;
import spring.lims.audit.sql.AuditSqlManager;
import spring.lims.audit.sql.AuditSqlProvider;
import spring.lims.audit.util.AuditAnnotationReader;
import org.springframework.beans.factory.config.BeanDefinition;

import java.util.List;
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
            Set<BeanDefinition> entities = annotationReader.getAnnotationBeans(AuditEntity.class);

            for (BeanDefinition bd : entities) {
                String className = bd.getBeanClassName();
                Class<?> entityClazz = Class.forName(className);
                AuditEntity entityAnnotation = entityClazz.getAnnotation(AuditEntity.class);
                List<String> idFields = annotationReader.getIdFieldNames(entityClazz);

                String selectClause = sqlProvider.generateSelectClause(entityClazz, entityAnnotation.name());

                SqlEntity entity = new SqlEntity();
                entity.setName(className);
                entity.setTarget(entityClazz);
                entity.setIdFields(idFields);
                entity.setSelectClause(selectClause);

                sqlManager.put(className, entity);
            }
        } catch(ClassNotFoundException e) {
            throw new RuntimeException("Not found AuditEntity. " + e.getMessage());
        }
    }

}