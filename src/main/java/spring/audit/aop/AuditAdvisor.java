package spring.audit.aop;

import spring.audit.config.AuditBeanConfig;
import spring.audit.config.AuditConfigurer;
import spring.audit.context.AuditManager;
import spring.audit.event.AuditEventListener;
import spring.audit.event.AuditTransactionListener;
import spring.audit.sql.AuditSqlRepository;
import spring.audit.util.AuditAnnotationReader;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import javax.sql.DataSource;

public class AuditAdvisor {
    private final AuditManager auditManager;
    private final AuditSqlRepository repository;
    private final AuditTransactionListener transactionListener;
    private final AuditAnnotationReader annotationReader;

    public AuditAdvisor(DataSource dataSource, AuditConfigurer configurer, AuditEventListener eventListener) {
        AuditBeanConfig beanConfig = new AuditBeanConfig(dataSource, configurer, eventListener);
        this.auditManager = beanConfig.auditManager();
        this.repository = beanConfig.sqlRepository();
        this.transactionListener = beanConfig.transactionListener();
        this.annotationReader = beanConfig.annotationReader();
    }

    public Advisor create() {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(spring.audit.annotation.Audit)");
        return new DefaultPointcutAdvisor(annotationPointcut, new AuditAdvice(auditManager, repository, transactionListener, annotationReader));
    }

}