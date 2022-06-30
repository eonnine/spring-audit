package spring.audit.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import spring.audit.annotation.Audit;
import spring.audit.annotation.AuditEntity;
import spring.audit.domain.AuditAttribute;
import spring.audit.domain.AuditMeta;
import spring.audit.domain.SqlRow;
import spring.audit.event.AuditTransactionListener;
import spring.audit.event.AuditTransactionManager;
import spring.audit.sql.AuditSqlRepository;
import spring.audit.util.AuditAnnotationReader;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnnotationAuditMethodInvocation implements MethodInvocation {

    private final MethodInvocation target;

    private final AuditManager auditManager;
    private final AuditSqlRepository repository;
    private final AuditAnnotationReader annotationReader;
    private final AuditTransactionListener transactionListener;

    public AnnotationAuditMethodInvocation(MethodInvocation invocation, AuditManager auditManager, AuditSqlRepository repository,
                           AuditAnnotationReader annotationReader, AuditTransactionListener transactionListener) {
        this.target = invocation;
        this.auditManager = auditManager;
        this.repository = repository;
        this.annotationReader = annotationReader;
        this.transactionListener = transactionListener;
    }

    @Override
    public Object proceed() throws Throwable {
        if (!AuditTransactionManager.isCurrentTransactionActive()) {
            initSynchronizeTransaction();
        }

        Method method = getMethod();

        if (!isInteger(method.getReturnType())) {
            return target.proceed();
        }

        AuditMeta meta = getAuditMetadata(method);

        if (isPreSnapshotTarget(meta.getAuditManagerKey())) {
            preSnapshot(meta);
        }

        Object result = target.proceed();
        if (isPostSnapshotTarget(result)) {
            postSnapshot(meta);
        }

        return result;
    }

    private void initSynchronizeTransaction() {
        AuditTransactionManager.initCurrentTransaction();
        AuditTransactionManager.bindListener(transactionListener);
    }

    private void preSnapshot(AuditMeta meta) {
        Map<String, Object> idParam = convertArgsToIdMap(meta.getEntityClazz(), getArguments());
        List<SqlRow> originData = repository.findAllById(meta.getSqlManagerKey(), idParam);
        AuditAttribute attribute = new AuditAttribute();
        attribute.setOriginRows(originData);
        auditManager.put(meta.getAuditManagerKey(), attribute);
    }

    private void postSnapshot(AuditMeta meta) {
        Map<String, Object> idParam = convertArgsToIdMap(meta.getEntityClazz(), getArguments());
        AuditAttribute attribute = auditManager.get(meta.getAuditManagerKey());
        List<SqlRow> updatedData = repository.findAllById(meta.getSqlManagerKey(), idParam);

        attribute.setLabel(meta.getAuditAnnotation().label());
        attribute.setTitle(meta.getAuditAnnotation().title());
        attribute.setId(idParam);
        attribute.setParameter(convertArgsToMap(meta.getEntityClazz(), getArguments()));
        attribute.setUpdatedRows(updatedData);
    }

    private AuditMeta getAuditMetadata(Method method) {
        Audit auditAnnotation = annotationReader.getAuditAnnotation(method);
        Class<?> entityClazz = annotationReader.getAuditEntity(auditAnnotation);
        AuditEntity entityAnnotation = annotationReader.getAuditEntityAnnotation(entityClazz);
        Map<String, Object> entityId = convertArgsToIdMap(entityClazz, getArguments());

        AuditMeta meta = new AuditMeta();
        meta.setAuditAnnotation(auditAnnotation);
        meta.setEntityClazz(entityClazz);
        meta.setAuditManagerKey(generateAuditKey(entityId, entityClazz));
        meta.setSqlManagerKey(entityAnnotation.name());
        return meta;
    }

    private boolean isPreSnapshotTarget(String key) {
        return !auditManager.has(key);
    }

    private boolean isPostSnapshotTarget(Object result) {
        return isUpdated(result);
    }

    private boolean isInteger(Object v) {
        return v == Integer.class || v == int.class;
    }

    private boolean isUpdated(Object v) {
        return v instanceof Integer && ((Integer) v) > 0;
    }

    private String generateAuditKey(Map<String, Object> parameterMap, Class<?> entityClazz) {
        String separator = "$";
        String paramValue = parameterMap.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(separator));
        return entityClazz.getName() + separator + paramValue;
    }

    private Map<String, Object> convertArgsToIdMap(Class<?> entityClazz, Object[] args) {
        List<String> idFieldNames = annotationReader.getIdFieldNames(entityClazz);
        return convertArgsToMap(entityClazz, args).entrySet().stream()
                .filter(entry -> idFieldNames.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<String, Object> convertArgsToMap(Class<?> entityClazz, Object[] args) {
        assertExistsParameter(args);
        Object arg = args[0];
        if (isPrimitiveParam(arg)) {
            return convertPrimitiveParamToMap(entityClazz, arg);
        }
        return convertObjectToMap(arg);
    }

    private Map<String, Object> convertPrimitiveParamToMap(Class<?> entityClazz, Object arg) {
        String value = String.valueOf(arg);
        List<String> fieldNames = annotationReader.getIdFieldNames(entityClazz);
        if (fieldNames.size() > 1) {
            throw new IllegalArgumentException("The parameter '" + value + "' can't be matched bacause there is more than one id field in the '" + entityClazz.getSimpleName() + "'. [" + entityClazz.getName() + "]");
        }
        return fieldNames.stream().collect(Collectors.toMap(s -> s, s -> value));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertObjectToMap(Object arg) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(arg, Map.class);
    }

    private boolean isPrimitiveParam(Object arg) {
        return arg instanceof String || arg instanceof Integer || arg instanceof Boolean;
    }

    private void assertExistsParameter(Object[] args) {
        if (args.length > 0) {
            return;
        }
        throw new IllegalArgumentException("Not found sql parameters. Parameter is required. [" + TransactionSynchronizationManager.getCurrentTransactionName() + "]");
    }

    @Override
    public Method getMethod() {
        return target.getMethod();
    }

    @Override
    public Object[] getArguments() {
        return target.getArguments();
    }

    @Override
    public Object getThis() {
        return target.getThis();
    }

    @Override
    public AccessibleObject getStaticPart() {
        return target.getStaticPart();
    }
}