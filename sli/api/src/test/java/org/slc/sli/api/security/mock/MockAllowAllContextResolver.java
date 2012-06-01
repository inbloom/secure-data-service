package org.slc.sli.api.security.mock;

import java.util.HashMap;
import java.util.Map;

import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.AllowAllEntityContextResolver;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;

/**
 * This is a mock class that truly allows all.  It should only be used for mocking unit test behavior when
 * the context resolver doesn't matter.
 * @author jnanney
 *
 */
public class MockAllowAllContextResolver extends AllowAllEntityContextResolver {
    
    
    @Override
    public boolean canResolve(String fromEntity, String toEntity) {
        return true;
    }
    
    public static void injectThis(ContextResolverStore store) {
        ApplicationContext mockContext = Mockito.mock(ApplicationContext.class);
        Map<String, EntityContextResolver> mockMap = new HashMap<String, EntityContextResolver>();
        mockMap.put("", new MockAllowAllContextResolver());
        Mockito.when(mockContext.getBeansOfType(EntityContextResolver.class)).thenReturn(mockMap);
        store.setApplicationContext(mockContext);

    }
    
    
}
