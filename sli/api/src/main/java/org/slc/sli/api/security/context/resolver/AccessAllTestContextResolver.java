package org.slc.sli.api.security.context.resolver;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.context.ContextResolverStore;

/**
 * A context resolver that should be used with the access-all-staff from the SecurityContextInjector.
 */
@Component
public class AccessAllTestContextResolver extends AllowAllEntityContextResolver {
    
    
    @Override
    public boolean canResolve(String fromEntity, String toEntity) {
        return ("access-all-staff".equals(fromEntity));
    }
    
    /**
     * This injects a allow all context resolver into the resolver store.  This was done so that
     * unit tests would pass the checkReferences() test in BasicService.  
     * @param store
     */
    public static void injectThis(ContextResolverStore store) {
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        Map<String, EntityContextResolver> mockMap = new HashMap<String, EntityContextResolver>();
        mockMap.put("", new AccessAllTestContextResolver());
        Mockito.when(mockContext.getBeansOfType(EntityContextResolver.class)).thenReturn(mockMap);
        store.setApplicationContext(mockContext);
    }
    
    
}
