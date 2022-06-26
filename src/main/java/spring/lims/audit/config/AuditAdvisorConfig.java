package spring.lims.audit.config;

import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import spring.lims.audit.aop.AuditAdvisor;
import spring.lims.audit.event.AuditEventListener;
import spring.lims.audit.event.DefaultAuditEventListener;

import javax.sql.DataSource;

public class AuditAdvisorConfig {

    @Bean
    @ConditionalOnMissingBean
    public AuditEventListener auditEventListener() {
        return new DefaultAuditEventListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditConfigurer auditConfigurer() {
        return new DefaultAuditConfigurer();
    }

    @Bean
    public Advisor auditTrailAdvisor(DataSource dataSource, ApplicationContext context) {
        AuditConfigurer configurer = context.getBean(AuditConfigurer.class);
        AuditEventListener listener = context.getBean(AuditEventListener.class);
        return new AuditAdvisor(dataSource, configurer, listener).create();
    }

}
