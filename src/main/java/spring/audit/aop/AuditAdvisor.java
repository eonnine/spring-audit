package spring.audit.aop;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.ApplicationContext;
import spring.audit.config.AuditBeanConfig;
import spring.audit.context.AuditApplicationContextAware;

import javax.sql.DataSource;

public class AuditAdvisor {

    public Advisor create(ApplicationContext context, DataSource dataSource) {
        AspectJExpressionPointcut annotationPointcut = new AspectJExpressionPointcut();
        annotationPointcut.setExpression("@annotation(spring.audit.annotation.Audit)");
        AuditBeanConfig beanConfig = new AuditBeanConfig(new AuditApplicationContextAware(context), dataSource);
        return new DefaultPointcutAdvisor(annotationPointcut, beanConfig.auditAdvice());
    }

}