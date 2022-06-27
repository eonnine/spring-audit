package spring.lims.audit.condition;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import spring.lims.audit.config.AuditConfigurer;

public class AuditConfigurerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            return context.getBeanFactory().getBean(AuditConfigurer.class) == null;
        } catch(NoSuchBeanDefinitionException e) {
            return true;
        }
    }
}