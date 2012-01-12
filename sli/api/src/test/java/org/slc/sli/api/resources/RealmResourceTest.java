package org.slc.sli.api.resources;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.config.EntityDefinition;
import org.slc.sli.api.config.EntityDefinitionStore;
import org.slc.sli.api.representation.EntityBody;
import org.slc.sli.api.resources.admin.RealmResource;
import org.slc.sli.api.service.EntityService;
import org.slc.sli.api.test.WebContextTestExecutionListener;

/**
 * Tests Realm info fetch and resolve to ssoInit URL
 * 
 * @author dkornishev
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class RealmResourceTest {
    
    private static final String ENTITY_TYPE_NAME = "realm";
    private static final String VALID_REALM_ID = "Calradia";
    private static final String INVALID_REALM_ID = "Mordor";
    private static final String IDP_ID_EXPECTED = "http://devdanil.slidev.org:8080/idp";
    
    @Autowired
    private RealmResource realmer;

    @Autowired
    private SecurityContextInjector securityContextInjector;
    
    @Autowired
    @Value("${sli.security.sso.url}")
    private String ssoInitUrl;
    
    private Map<String, EntityBody> entities;
    
    @Before
    public void init() {
        // inject administrator security context for unit testing
        securityContextInjector.setAdminContext();
        
        entities = new HashMap<String, EntityBody>();
        
        EntityBody entity = new EntityBody();
        entity.put("id", VALID_REALM_ID);
        entity.put("state", "nc");
        entity.put("idp", IDP_ID_EXPECTED);
        
        entities.put(VALID_REALM_ID, entity);
        
        realmer.setStore(mockStore());
    }
    
    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }
    
    @Test
    public void testRealmList() {
        Set<EntityBody> realms = realmer.getRealms();
        
        Assert.assertNotNull(realms);
        Assert.assertTrue(realms.size() > 0);
        
        for (EntityBody eb : realms) {
            EntityBody expected = this.entities.get(eb.get("id"));
            
            if (expected != null) {
                Assert.assertEquals(expected.get("idp"), eb.get("idp"));
                Assert.assertEquals(expected.get("state"), eb.get("state"));
            }
        }
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSSONothing() {
        String ssoInit = realmer.getSsoInitUrl(null);
        
        Assert.assertNull(ssoInit);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSSOInvalidId() {
        
        String ssoInit = realmer.getSsoInitUrl(INVALID_REALM_ID);
        
        Assert.assertNull(ssoInit);
    }
    
    @Test
    public void testSSOValidId() throws Exception {
        
        String ssoInit = realmer.getSsoInitUrl(VALID_REALM_ID);
        
        Assert.assertNotNull(ssoInit);
        Assert.assertEquals(this.ssoInitUrl.replaceAll("\\{idpId\\}", URLEncoder.encode(IDP_ID_EXPECTED, "UTF-8")),
                ssoInit);
    }
    
    private EntityDefinitionStore mockStore() {
        
        EntityDefinitionStore store = Mockito.mock(EntityDefinitionStore.class);
        EntityDefinition ed = Mockito.mock(EntityDefinition.class);
        EntityService es = Mockito.mock(EntityService.class);
        
        Mockito.when(ed.getService()).thenReturn(es);
        Mockito.when(ed.getType()).thenReturn(ENTITY_TYPE_NAME);
        
        Mockito.when(store.lookupByResourceName(ENTITY_TYPE_NAME)).thenReturn(ed);
        
        Mockito.when(es.getEntityDefinition()).thenReturn(ed);
        Mockito.when(es.list(0, 9999)).thenReturn(Arrays.asList(VALID_REALM_ID));
        
        EntityBody entity = this.entities.get(VALID_REALM_ID);
        
        Mockito.when(es.get(VALID_REALM_ID)).thenReturn(entity);
        Mockito.when(es.get(Arrays.asList(VALID_REALM_ID))).thenReturn(Arrays.asList(entity));
        
        return store;
    }
}
