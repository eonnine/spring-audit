package spring.audit.config;

import spring.audit.aop.AuditAdvisor;
import spring.audit.event.AuditEventListener;
import spring.audit.event.DefaultAuditEventListener;
import org.springframework.aop.Advisor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class AuditAdvisorConfig {

    @Bean
    public static Advisor auditTrailAdvisor(DataSource dataSource, ApplicationContext context) {
        return new AuditAdvisor(dataSource, auditConfigurer(context), auditEventListener(context)).create();
    }

    public static AuditConfigurer auditConfigurer(ApplicationContext context) {
        return existsBean(context, AuditConfigurer.class) ? context.getBean(AuditConfigurer.class) : new DefaultAuditConfigurer();
    }

    public static AuditEventListener auditEventListener(ApplicationContext context) {
        return existsBean(context, AuditEventListener.class) ? context.getBean(AuditEventListener.class) : new DefaultAuditEventListener();
    }

    private static boolean existsBean(ApplicationContext context, Class<?> clazz) {
        return !context.getBeansOfType(clazz).isEmpty();
    }

}