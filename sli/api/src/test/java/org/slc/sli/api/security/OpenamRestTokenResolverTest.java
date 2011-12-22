package org.slc.sli.api.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.security.enums.DefaultRoles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import org.slc.sli.api.security.mock.Mocker;
import org.slc.sli.api.security.openam.OpenamRestTokenResolver;


/**
 * Unit tests for the OpenamRestTokenResolver.
 *
 */
public class OpenamRestTokenResolverTest {
    
    
    private OpenamRestTokenResolver resolver;
    
    @Before
    public void init() {
        resolver = new OpenamRestTokenResolver();
        resolver.setTokenServiceUrl(Mocker.MOCK_URL);
        resolver.setRest(Mocker.mockRest());
    }
    
    @Test
    public void testResolveSuccess() {
        Authentication auth = resolver.resolve(Mocker.VALID_TOKEN);
        Assert.assertNotNull(auth);
        Assert.assertTrue(auth.getAuthorities().contains(new GrantedAuthorityImpl(DefaultRoles.ADMINISTRATOR.getSpringRoleName())));
    }
    
    @Test
    public void testResolveFailure() {
        Authentication auth = resolver.resolve(Mocker.INVALID_TOKEN);
        Assert.assertNull(auth);
    }
}
