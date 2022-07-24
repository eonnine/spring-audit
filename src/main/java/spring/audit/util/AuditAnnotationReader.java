package spring.audit.util;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationConfigurationException;
import spring.audit.annotation.Audit;
import spring.audit.annotation.AuditEntity;
import spring.audit.annotation.AuditId;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import spring.audit.context.AuditApplicationContextAware;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AuditAnnotationReader {

    private final ApplicationContext context;

    public AuditAnnotationReader(ApplicationContext context) {
        this.context = context;
    }

    public List<Field> getIdFields(Class<?> clazz) {
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(AuditId.class))
                .collect(Collectors.toList());
        if (fields.isEmpty()) {
            throw new AnnotationConfigurationException("There is no field with 'AuditId' annotation in the '" + clazz.getSimpleName() + "'. [" + clazz.getName() + "]");
        }
        return fields;
    }

    public List<String> getIdFieldNames(Class<?> clazz) {
        return getIdFields(clazz).stream().map(Field::getName).collect(Collectors.toList());
    }

    public Audit getAuditAnnotation(Method method) {
        assertHasAuditAnnotation(method);
        return method.getAnnotation(Audit.class);
    }

    public AuditEntity getAuditEntityAnnotation(Class<?> entityClazz) {
        assertHasAuditEntityAnnotation(entityClazz);
        return entityClazz.getAnnotation(AuditEntity.class);
    }

    public Class<?> getAuditEntity(Audit audit) {
        Class<?> clazz = audit.target();
        assertHasAuditEntityAnnotation(clazz);
        return clazz;
    }

    public Map<String, Object> getAnnotationBeans(Class<? extends Annotation> annotationClazz) {
        return context.getBeansWithAnnotation(AuditEntity.class);
    }

    public Set<BeanDefinition> getAnnotationBeanDefinitions(Class<? extends Annotation> annotationClazz) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClazz));
        return scanner.findCandidateComponents("**/*");
    }

    private void assertHasAuditAnnotation(Method method) {
        if (method != null && method.isAnnotationPresent(Audit.class)) {
            return;
        }
        assert method != null;
        throw new AnnotationConfigurationException("Method '" + method.getName() + "' has not 'Audit' annotation. [" + method.getDeclaringClass().getName() + "]");
    }

    private void assertHasAuditEntityAnnotation(Class<?> entityClazz) {
        if (entityClazz != null && entityClazz.isAnnotationPresent(AuditEntity.class)) {
            return;
        }
        assert entityClazz != null;
        throw new AnnotationConfigurationException("Class '" + entityClazz.getSimpleName() + "' has not 'AuditEntity' annotation. [" + entityClazz.getName() + "]");
    }

}