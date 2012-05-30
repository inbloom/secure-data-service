package org.slc.sli.api.security.oauth;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.client.constants.EntityNames;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.ContextResolverStore;
import org.slc.sli.api.security.context.resolver.EntityContextResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 *
 * @author pwolf
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class ApplicationAuthorizationValidatorTest {

    @Autowired
    @InjectMocks
    ApplicationAuthorizationValidator validator;

    @Mock
    Repository<Entity> repo;

    @Mock
    ContextResolverStore store;

    @Mock
    EntityContextResolver resolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        //set up the resolver store to resolve a couple of edorgs
        List<String> edOrgIds = new ArrayList<String>();
        edOrgIds.add("district1");
        edOrgIds.add("school1");
        Mockito.when(resolver.findAccessible(Mockito.any(Entity.class))).thenReturn(edOrgIds);
        Mockito.when(store.findResolver(EntityNames.TEACHER, EntityNames.EDUCATION_ORGANIZATION)).thenReturn(resolver);

        //Set up the LEA
        HashMap body = new HashMap();
        List<String> categories = new ArrayList<String>();
        categories.add("Local Education Agency");
        Entity district1 = new MongoEntity("educationOrganization", "district1", body, new HashMap<String, Object>());
        district1.getBody().put("stateOrganizationId", "NC-D1");
        district1.getBody().put("organizationCategories", categories);
        Mockito.when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "district1")).thenReturn(district1);

        //Set up a school
        body = new HashMap();
        categories = new ArrayList<String>();
        categories.add("School");
        Entity school1 = new MongoEntity("educationOrganization", "school1", body, new HashMap<String, Object>());
        school1.getBody().put("organizationCategories", categories);
        school1.getBody().put("stateOrganizationId", "NC-D1-SC1");
        Mockito.when(repo.findById(EntityNames.EDUCATION_ORGANIZATION, "school1")).thenReturn(school1);

    }

    @Test
    public void testAppAuthorizationNoAppAuth() {
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(new MongoEntity("teacher", "teacherUniqueId", new HashMap<String, Object>(), new HashMap<String, Object>()));
        assertNull(validator.getAuthorizedApps(principal));
    }

    @Test
    public void testAppIsAuthorized() {

        //Create an auth token to use
        SLIPrincipal principal = new SLIPrincipal();
        principal.setEntity(new MongoEntity("teacher", "teacherUniqueId", new HashMap<String, Object>(), new HashMap<String, Object>()));

        //Register an app list with district1 containing the requested app
        Entity appAuthEnt = new MongoEntity("applicationAuthorization", new HashMap<String, Object>());
        appAuthEnt.getBody().put("authId", "NC-D1");
        appAuthEnt.getBody().put("authType", "EDUCATION_ORGANIZATION");
        List<String> allowedApps = new ArrayList<String>();
        allowedApps.add("appId");
        appAuthEnt.getBody().put("appIds", allowedApps);
        Mockito.when(repo.findOne(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(appAuthEnt);
        List<Entity> entities = new ArrayList<Entity>();
        Entity mockEntity = Mockito.mock(Entity.class);
        Mockito.when(mockEntity.getEntityId()).thenReturn("appId");
        entities.add(mockEntity);
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(entities);

        assertTrue("Authorized app list should contain appId", validator.getAuthorizedApps(principal).contains("appId"));
    }

}
