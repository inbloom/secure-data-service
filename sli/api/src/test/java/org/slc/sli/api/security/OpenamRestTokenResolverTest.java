<<<<<<< HEAD
package org.slc.sli.api.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.web.client.RestTemplate;

import org.slc.sli.api.security.openam.OpenamRestTokenResolver;

public class OpenamRestTokenResolverTest {
    
    private static final String     MOCK_URL      = "mock";
    private static final String     VALID_TOKEN   = "valid_token";
    private static final String     INVALID_TOKEN = "invalid_token";
    
    private static final String     PAYLOAD       = "userdetails.token.id=AQIC5wM2LY4Sfczq7rqe5T89hyJzxvtfmlagB2VFZEFxE2I.*AAJTSQACMDE.*\r\n" + "userdetails.role=id=student,ou=group,dc=opensso,dc=java,dc=net\r\n"
                                                          + "userdetails.role=id=parent,ou=group,dc=opensso,dc=java,dc=net\r\n" + "userdetails.role=id=teacher,ou=group,dc=opensso,dc=java,dc=net\r\n" + "userdetails.attribute.name=uid\r\n"
                                                          + "userdetails.attribute.value=demo\r\n" + "userdetails.attribute.name=userpassword\r\n" + "userdetails.attribute.value={SSHA}lDuocwqvaaTzkN5Bi+beD2BJBP4sI/dtzD/BfQ==\r\n"
                                                          + "userdetails.attribute.name=sn\r\n" + "userdetails.attribute.value=demo\r\n" + "userdetails.attribute.name=cn\r\n" + "userdetails.attribute.value=demo demo\r\n"
                                                          + "userdetails.attribute.name=givenname\r\n" + "userdetails.attribute.value=demo\r\n" + "userdetails.attribute.name=inetuserstatus\r\n" + "userdetails.attribute.value=Active\r\n"
                                                          + "userdetails.attribute.name=dn\r\n" + "userdetails.attribute.value=uid=demo,ou=people,dc=slidev,dc=net\r\n" + "userdetails.attribute.name=objectclass\r\n"
                                                          + "userdetails.attribute.value=person\r\n" + "userdetails.attribute.value=sunIdentityServerLibertyPPService\r\n" + "userdetails.attribute.value=inetorgperson\r\n"
                                                          + "userdetails.attribute.value=sunFederationManagerDataStore\r\n" + "userdetails.attribute.value=iPlanetPreferences\r\n"
                                                          + "userdetails.attribute.value=iplanet-am-auth-configuration-service\r\n" + "userdetails.attribute.value=organizationalperson\r\n"
                                                          + "userdetails.attribute.value=sunFMSAML2NameIdentifier\r\n" + "userdetails.attribute.value=inetuser\r\n" + "userdetails.attribute.value=iplanet-am-managed-person\r\n"
                                                          + "userdetails.attribute.value=iplanet-am-user-service\r\n" + "userdetails.attribute.value=sunAMAuthAccountLockout\r\n" + "userdetails.attribute.value=top";
    
    private OpenamRestTokenResolver resolver;
    
    @Before
    public void init() {
        resolver = new OpenamRestTokenResolver();
        resolver.setTokenServiceUrl(MOCK_URL);
        resolver.setRest(mockRest());
    }
    
    @Test
    public void testResolveSuccess() {
        Authentication auth = resolver.resolve(VALID_TOKEN);
        Assert.assertNotNull("Authentication object is null",auth);
        Assert.assertTrue("Authorities are missing ROLE_USER",auth.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_USER")));
        Assert.assertEquals("Wrong Token",VALID_TOKEN, auth.getCredentials());
        Assert.assertNotNull("No Principal in authentication",auth.getPrincipal());
        
        SLIPrincipal principal = (SLIPrincipal) auth.getPrincipal();
        
        Assert.assertEquals("Wrong id for principal","demo", principal.getId());
        Assert.assertEquals("Wrong name for principal","demo demo", principal.getName());
    }
    
    @Test
    public void testResolveFailure() {
        Authentication auth = resolver.resolve(INVALID_TOKEN);
        Assert.assertNull(auth);
    }
    
    private RestTemplate mockRest() {
        RestTemplate rest = mock(RestTemplate.class);
        
        ResponseEntity<String> validationOK = new ResponseEntity<String>("boolean=true", HttpStatus.OK);
        ResponseEntity<String> validationFail = new ResponseEntity<String>("boolean=true", HttpStatus.UNAUTHORIZED);
        ResponseEntity<String> attributesOK = new ResponseEntity<String>(PAYLOAD, HttpStatus.OK);
        
        when(rest.getForEntity(MOCK_URL + "/identity/isTokenValid?tokenid=" + VALID_TOKEN, String.class, Collections.<String, Object> emptyMap())).thenReturn(validationOK);
        when(rest.getForEntity(MOCK_URL + "/identity/isTokenValid?tokenid=" + INVALID_TOKEN, String.class, Collections.<String, Object> emptyMap())).thenReturn(validationFail);
        when(rest.getForEntity(MOCK_URL + "/identity/attributes?subjectid=" + VALID_TOKEN, String.class, Collections.<String, Object> emptyMap())).thenReturn(attributesOK);
        
        return rest;
    }
}
=======
package org.slc.sli.api.security;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slc.sli.api.security.openam.OpenamRestTokenResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.web.client.RestTemplate;

public class OpenamRestTokenResolverTest {
    
    private static final String MOCK_URL = "mock";
    private static final String VALID_TOKEN = "valid_token";
    private static final String INVALID_TOKEN = "invalid_token";
    
    private static final String PAYLOAD = "userdetails.token.id=AQIC5wM2LY4Sfczq7rqe5T89hyJzxvtfmlagB2VFZEFxE2I.*AAJTSQACMDE.*\r\n"
            + "userdetails.role=id=student,ou=group,dc=opensso,dc=java,dc=net\r\n"
            + "userdetails.role=id=parent,ou=group,dc=opensso,dc=java,dc=net\r\n"
            + "userdetails.role=id=teacher,ou=group,dc=opensso,dc=java,dc=net\r\n"
            + "userdetails.attribute.name=uid\r\n"
            + "userdetails.attribute.value=demo\r\n"
            + "userdetails.attribute.name=userpassword\r\n"
            + "userdetails.attribute.value={SSHA}lDuocwqvaaTzkN5Bi+beD2BJBP4sI/dtzD/BfQ==\r\n"
            + "userdetails.attribute.name=sn\r\n"
            + "userdetails.attribute.value=demo\r\n"
            + "userdetails.attribute.name=cn\r\n"
            + "userdetails.attribute.value=demo demo\r\n"
            + "userdetails.attribute.name=givenname\r\n"
            + "userdetails.attribute.value=demo\r\n"
            + "userdetails.attribute.name=inetuserstatus\r\n"
            + "userdetails.attribute.value=Active\r\n"
            + "userdetails.attribute.name=dn\r\n"
            + "userdetails.attribute.value=uid=demo,ou=people,dc=slidev,dc=net\r\n"
            + "userdetails.attribute.name=objectclass\r\n"
            + "userdetails.attribute.value=person\r\n"
            + "userdetails.attribute.value=sunIdentityServerLibertyPPService\r\n"
            + "userdetails.attribute.value=inetorgperson\r\n"
            + "userdetails.attribute.value=sunFederationManagerDataStore\r\n"
            + "userdetails.attribute.value=iPlanetPreferences\r\n"
            + "userdetails.attribute.value=iplanet-am-auth-configuration-service\r\n"
            + "userdetails.attribute.value=organizationalperson\r\n"
            + "userdetails.attribute.value=sunFMSAML2NameIdentifier\r\n"
            + "userdetails.attribute.value=inetuser\r\n"
            + "userdetails.attribute.value=iplanet-am-managed-person\r\n"
            + "userdetails.attribute.value=iplanet-am-user-service\r\n"
            + "userdetails.attribute.value=sunAMAuthAccountLockout\r\n" + "userdetails.attribute.value=top";
    
    private OpenamRestTokenResolver resolver;
    
    @Before
    public void init() {
        resolver = new OpenamRestTokenResolver();
        resolver.setTokenServiceUrl(MOCK_URL);
        resolver.setRest(mockRest());
    }
    
    @Test
    public void testResolveSuccess() {
        Authentication auth = resolver.resolve(VALID_TOKEN);
        Assert.assertNotNull(auth);
        Assert.assertTrue(auth.getAuthorities().contains(new GrantedAuthorityImpl("ROLE_USER")));
    }
    
    @Test
    public void testResolveFailure() {
        Authentication auth = resolver.resolve(INVALID_TOKEN);
        Assert.assertNull(auth);
    }
    
    private RestTemplate mockRest() {
        RestTemplate rest = mock(RestTemplate.class);
        
        ResponseEntity<String> validationOK = new ResponseEntity<String>("boolean=true", HttpStatus.OK);
        ResponseEntity<String> validationFail = new ResponseEntity<String>("boolean=true", HttpStatus.UNAUTHORIZED);
        ResponseEntity<String> attributesOK = new ResponseEntity<String>(PAYLOAD, HttpStatus.OK);
        
        when(
                rest.getForEntity(MOCK_URL + "/identity/isTokenValid?tokenid=" + VALID_TOKEN, String.class,
                        Collections.emptyMap())).thenReturn(validationOK);
        when(
                rest.getForEntity(MOCK_URL + "/identity/isTokenValid?tokenid=" + INVALID_TOKEN, String.class,
                        Collections.emptyMap())).thenReturn(validationFail);
        when(
                rest.getForEntity(MOCK_URL + "/identity/attributes?subjectid=" + VALID_TOKEN, String.class,
                        Collections.emptyMap())).thenReturn(attributesOK);
        
        return rest;
    }
}
>>>>>>> 3bd1c345402a304d47c6039d71289bc3b2aee5ea
