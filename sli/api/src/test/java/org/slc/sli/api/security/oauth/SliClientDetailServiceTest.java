package org.slc.sli.api.security.oauth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.NeutralQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

/**
 * Class to test SliClientDetailService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class SliClientDetailServiceTest {
    @Autowired
    private SliClientDetailService detailsService;

    private EntityService service;

    @Before
    public void setUp() throws Exception {
        service = mock(EntityService.class);
        detailsService.setService(service);
    }

    @Test
    public void testLoadClientByClientId() throws Exception {
        String clientId = "1234567890";
        String uuid = "123";
        ArrayList<String> existingEntitiesIds = new ArrayList<String>();
        existingEntitiesIds.add(uuid);
        Mockito.when(
                service.listIds(any(NeutralQuery.class)))
                .thenReturn(existingEntitiesIds);

        EntityBody mockApp = new EntityBody();
        mockApp.put("client_id", clientId);
        mockApp.put("id", uuid);
        mockApp.put("client_secret", "ldkafjladsfjdsalfadsl");
        mockApp.put("redirect_uri", "https://slidev.org");
        mockApp.put("scope", "PUBLIC");
        Mockito.when(service.get(uuid)).thenReturn(mockApp);

        ClientDetails details = detailsService.loadClientByClientId(clientId);
        assertNotNull(details);
        assertNotNull("Checking for client id", details.getClientId());
        assertNotNull("Checking for client secret", details.getClientSecret());
        assertNotNull("Checking for redirect uri", details.getWebServerRedirectUri());
        assertNotNull("Checking for scope", details.getScope());
    }

    @Test(expected = OAuth2Exception.class)
    public void testBadClientLookup() {
        String clientId = "1234567890";
        //return empty list
        Mockito.when(
                service.listIds(any(NeutralQuery.class)))
                .thenReturn(new ArrayList<String>());
        detailsService.loadClientByClientId(clientId);
    }

}
