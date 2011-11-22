package org.slc.sli.api.security;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Provides a permission evaluator that can be used to determine whether or not the principal in the
 * security context has
 * the specific permission on the target object.
 * 
 * Because the SLI data model defines relationships between entities that are used to derive
 * effective privileges, this
 * class is a bit different than a standard ACL solution on domain objects.
 * 
 * @author Sean Melody <smelody@wgen.net>
 * 
 */
@Component
public class DynamicPermissionEvaluator implements PermissionEvaluator, ApplicationContextAware {
    
    /** Map of class names to evaluation strategies */
    
    private Map<String, PermissionEvaluationStrategy<?>> evaluationStrategies = new HashMap<String, PermissionEvaluationStrategy<?>>();
    private ApplicationContext applicationContext;
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        
        boolean bHasPermission = false; // deny by default
        
        if (targetDomainObject != null) {
            Class<? extends Object> clz = targetDomainObject.getClass();
            PermissionEvaluationStrategy<?> strategy = evaluationStrategies.get(clz.getName());
            if (strategy != null) {
                
                bHasPermission = strategy.hasPermission(authentication, targetDomainObject, permission);
            }
        }
        
        return bHasPermission;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
            Object permission) {
        
        boolean bHasPermission = false; // deny by default
        
        PermissionEvaluationStrategy<?> strategy = evaluationStrategies.get(targetType);
        if (strategy != null) {
            
            bHasPermission = strategy.hasPermission(authentication, targetId, targetType, permission);
        }
        
        return bHasPermission;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    @SuppressWarnings("rawtypes")
    @PostConstruct
    public void initStrategyMap() {
        
        Map<String, PermissionEvaluationStrategy> map = applicationContext
                .getBeansOfType(PermissionEvaluationStrategy.class);
        for (PermissionEvaluationStrategy<?> eval : map.values()) {
            evaluationStrategies.put(eval.getDomainClass().getName(), eval);
        }
    }
    
}
