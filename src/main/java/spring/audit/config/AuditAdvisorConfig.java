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
    public Advisor auditTrailAdvisor(ApplicationContext context, DataSource dataSource) {
        return new AuditAdvisor(context, dataSource).create();
    }

}