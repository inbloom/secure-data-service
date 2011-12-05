package org.slc.sli.api.security;

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

import org.slc.sli.api.security.openam.OpenamRestTokenResolver;

public class OpenamRestTokenResolverTest {
    
    private static final String     MOCK = "mock";
    
    private OpenamRestTokenResolver resolver;
    
    @Before
    public void init() {
        resolver = new OpenamRestTokenResolver();
        resolver.setTokenServiceUrl(MOCK);
        resolver.setRest(mockRest());
    }
    
    @Test
    public void testResolveSuccess() {
        
    }
    
    @Test
    public void testResolveFailure() {
        
    }
    
    private RestTemplate mockRest() {
        rest = mock(RestTemplate.class);
    }
    
}
