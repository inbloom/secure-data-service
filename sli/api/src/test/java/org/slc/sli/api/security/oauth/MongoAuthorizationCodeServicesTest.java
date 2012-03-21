package org.slc.sli.api.security.oauth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.Repository;

/**
 * Test class for MongoAuthorizationCodeServices class.
 * 
 * @author jnanney
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class MongoAuthorizationCodeServicesTest {
    
    @Mock
    private SliClientDetailService clientDetailsService;
    
    @Mock
    private Repository<Entity> repo;

    @Mock
    private RolesToRightsResolver roleResolver;

    @Mock
    private UserLocator userLocator;
    
    @InjectMocks
    private MongoAuthorizationCodeServices services;

    public static final String CLIENT_ID = "fake-client-id";
    public static final String CLIENT_SECRET = "fake-client-secret";
    public static final String REDIRECT_URI = "http://local.slidev.org";
    public static final String SAML_ID = "fake-SAML-ID";
    

    
    
    @Before
    public void setUp() {
        services = Mockito.mock(MongoAuthorizationCodeServices.class);
        clientDetailsService = Mockito.mock(SliClientDetailService.class);
        repo = Mockito.mock(Repository.class);
        roleResolver = Mockito.mock(RolesToRightsResolver.class);
        userLocator = Mockito.mock(UserLocator.class);
        ApplicationDetails mockClientDetails = new ApplicationDetails();
        mockClientDetails.setClientId(CLIENT_ID);
        mockClientDetails.setClientSecret(CLIENT_SECRET);
        mockClientDetails.setWebServerRedirectUri(REDIRECT_URI);
        mockClientDetails.setIsScoped(false);
        mockClientDetails.setIsSecretRequired(true);

        Mockito.when(clientDetailsService.loadClientByClientId(CLIENT_ID)).thenReturn(mockClientDetails);
    }
    
    @Test
    public void testCreate() {
        
        services.create(CLIENT_ID, "NC", REDIRECT_URI, SAML_ID);
        
    }
    
    @Test
    public void testCreateWithBadRedirectUri() {
        services.create(CLIENT_ID, "NC", "https://local.slidev.org", SAML_ID);
    }
    
    @Test
    public void testCreateAuthorizationCodeForMessageId() {
        
    }
    
    @Test
    public void testRemove() {
        
    }
}
