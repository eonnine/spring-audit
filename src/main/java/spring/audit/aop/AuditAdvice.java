package spring.audit.aop;

import spring.audit.context.AnnotationAuditMethodInvocation;
import spring.audit.context.AuditManager;
import spring.audit.event.AuditTransactionListener;
import spring.audit.sql.AuditSqlRepository;
import spring.audit.util.AuditAnnotationReader;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class AuditAdvice implements MethodInterceptor {
    private final AuditManager auditManager;
    private final AuditSqlRepository repository;
    private final AuditTransactionListener transactionListener;
    private final AuditAnnotationReader annotationReader;

    public AuditAdvice(AuditManager auditManager, AuditSqlRepository repository, AuditTransactionListener transactionListener, AuditAnnotationReader annotationReader) {
        this.auditManager = auditManager;
        this.repository = repository;
        this.transactionListener = transactionListener;
        this.annotationReader = annotationReader;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        MethodInvocation auditInvocation = new AnnotationAuditMethodInvocation(invocation, auditManager, repository, annotationReader, transactionListener);
        return auditInvocation.proceed();
    }
}