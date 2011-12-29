package org.slc.sli.security;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;

import org.slc.sli.security.mock.Mocker;
/**
 * 
 * TODO: Add javadocs
 *
 */
public class OpenamTokenResolverTest {


    private  OpenamRestTokenResolver resolver;
    
    @Before
    public void setup() {
        resolver = new OpenamRestTokenResolver();
        resolver.setTokenServiceUrl(Mocker.MOCK_URL);
        resolver.setRest(Mocker.mockRest());
    }
    
    @Test
    public void resolveSuccessTest() {
        Authentication auth = resolver.resolve(Mocker.VALID_TOKEN);
        Assert.assertNotNull(auth);
        Assert.assertTrue(auth.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_USER")));
    }
    
    
    @Test
    public void testResolveFailure() {
        Authentication auth = resolver.resolve(Mocker.INVALID_TOKEN);
        Assert.assertNull(auth);
    }

}
