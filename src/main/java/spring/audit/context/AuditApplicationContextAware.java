package spring.audit.context;

import org.springframework.context.ApplicationContext;

public class AuditApplicationContextAware {

    private final ApplicationContext context;

    public AuditApplicationContextAware(ApplicationContext context) {
        this.context = context;
    }

    public ApplicationContext getApplicationContext() {
        return this.context;
    }
}