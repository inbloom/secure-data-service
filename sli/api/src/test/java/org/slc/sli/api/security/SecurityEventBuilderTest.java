/*
 * Copyright 2012-2013 inBloom, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.slc.sli.api.security;

import com.google.common.collect.Sets;
import junit.framework.Assert;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.api.constants.ResourceNames;
import org.slc.sli.api.resources.SecurityContextInjector;
import org.slc.sli.api.security.context.EdOrgOwnershipArbiter;
import org.slc.sli.api.security.context.PagingRepositoryDelegate;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.context.resolver.RealmHelper;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.api.util.SecurityUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import java.net.URI;
import java.util.*;

import static org.mockito.Matchers.argThat;

/**
 * Utility class to fill in common SecurityEvent details
 */
@SuppressWarnings("ALL")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
@DirtiesContext
public class SecurityEventBuilderTest {

    private static final String REALM_EDORG_STATEID = "realmEdOrgStateId";

    private static final String EDORG_STATEID = "edOrgStateId";
    private static final String EDORG2_STATEID = "edOrg2StateId";

    private static final String LOG_MESSAGE = "security event log message";
    private static final String URI_ENTITY_RESOURCE_TYPE = ResourceNames.SECTIONS;
    private static final String URI_ENTITY_TYPE = EntityNames.SECTION;
    private static final String SECTION_ENTITY_ID = "00000000-abcd-0000-0000-000000000003_id";
    private static final String SECTION_ENTITY2_ID = "22222222-abcd-0000-0000-000000000003_id";

    private static final String ONE_PART_URI = "http://local.slidev.org:8080/api/rest/v1.2/"+ URI_ENTITY_RESOURCE_TYPE;
    private static final String TWO_PART_URI = "http://local.slidev.org:8080/api/rest/v1.2/"+ URI_ENTITY_RESOURCE_TYPE +"/"+ SECTION_ENTITY_ID;
    private static final String SIX_PART_URI = "http://local.slidev.org:8080/api/rest/v1.2/"+ URI_ENTITY_RESOURCE_TYPE +"/"+ SECTION_ENTITY_ID +"/studentSectionAssociations/students/studentParentAssociations/parents";

    private static final String TWO_PART_URI_MULTI_IDS = "http://local.slidev.org:8080/api/rest/v1.2/" + URI_ENTITY_RESOURCE_TYPE + "/" + SECTION_ENTITY_ID + "," + SECTION_ENTITY2_ID;

    @Autowired
    @InjectMocks
    private SecurityEventBuilder builder;

    @Mock
    private CallingApplicationInfoProvider callingApplicationInfoProvider;

    @Mock
    private RealmHelper realmHelper;

    @Mock
    private EdOrgHelper edOrgHelper;

    @Mock
    private PagingRepositoryDelegate<Entity> repository;

    @Mock
    private EdOrgOwnershipArbiter arbiter;

    @Autowired
    private SecurityContextInjector injector;

    SLIPrincipal principal;
    Entity edOrg1, edOrg2, section1, section2;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        injector.setEducatorContext();
        principal = SecurityUtil.getSLIPrincipal();
        principal.setRealmEdOrg(REALM_EDORG_STATEID);
//        principal.setTenantId("testTenantId");
        principal.setExternalId("testPrincipalExternalId");
//        principal.setSessionId("testPrincipalSessionId");
//        SecurityUtil.getSLIPrincipal().setAuthorizingEdOrgs(new HashSet<String>(Arrays.asList(myEdOrg.getEntityId())));

        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, EDORG_STATEID);
        edOrg1 = createEntity(EntityNames.SCHOOL, "edorg1", body);
        body = new HashMap<String, Object>();
        body.put(ParameterConstants.STATE_ORGANIZATION_ID, EDORG2_STATEID);
        edOrg2 = createEntity(EntityNames.SCHOOL, "edorg2", body);
        section1 = createEntity(EntityNames.SECTION, SECTION_ENTITY_ID, new HashMap<String, Object>());
        section2 = createEntity(EntityNames.SECTION, SECTION_ENTITY2_ID, new HashMap<String, Object>());

    }

    @After
    public void clean() {
        builder = null;
    }

    /**
     * Creates a security event with explicitly specified targetEdOrgList via targetEdOrgIds
     *
     * @param loggingClass              java class logging the security event
     * @param requestUri                relevant URI
     * @param slMessage                 security event message text
     * @param explicitPrincipal         used instead of the principle from the security context
     * @param explicitRealmEntity       used instead of the realm from the security context
     * @param targetEdOrgs              targetEdOrg stateOrganizationId values (note these are not db ids)
     * @param defaultTargetToUserEdOrg  whether or not to set targetEdOrgList to be userEdOrg by default
     * @return  security event
     */
//    public SecurityEvent createSecurityEvent(String loggingClass, URI requestUri, String slMessage, SLIPrincipal explicitPrincipal, Entity explicitRealmEntity,
//                                             Set<String> targetEdOrgs, boolean defaultTargetToUserEdOrg) {

    // test deriving targetEdOrg from a one part URI - no id (negative case)
    @Test
    public void testCreateSecurityEventFromOnePartUri() {
        SecurityEvent se = testTargetEdOrgFromUriHelper(ONE_PART_URI);
        Assert.assertTrue(se.getActionUri().equals(ONE_PART_URI));
        Assert.assertTrue(se.getLogMessage().equals(LOG_MESSAGE));
        Assert.assertTrue(se.getUserEdOrg().equals(principal.getRealmEdOrg()));
        Assert.assertTrue(se.getTargetEdOrgList().contains(principal.getRealmEdOrg()));
    }

    // test deriving targetEdOrg from a two part URI
    @Test
    public void testCreateSecurityEventFromTwoPartUri() {
        SecurityEvent se = testTargetEdOrgFromUriHelper(TWO_PART_URI);
        Assert.assertTrue(se.getTargetEdOrgList().contains(EDORG_STATEID));
    }

    // test deriving targetEdOrg from a six part URI
    @Test
    public void testCreateSecurityEventFromSixPartUri() {
        SecurityEvent se = testTargetEdOrgFromUriHelper(SIX_PART_URI);
        Assert.assertTrue(se.getTargetEdOrgList().contains(EDORG_STATEID));
    }

    // test deriving targetEdOrg from a URI with multiple ids
    @Test
    public void testCreateSecurityEventFromUriMutipleIds() {
        SecurityEvent se = testTargetEdOrgFromUriHelper(TWO_PART_URI_MULTI_IDS);
        Assert.assertTrue(containsTheSame(se.getTargetEdOrgList(), Arrays.asList(EDORG_STATEID, EDORG2_STATEID)));
    }

    // test setting the targetEdOrgList from the userEdOrg
    @Test
    public void testCreateSecurityEventUsingDefaultTargetToUserEdOrg() {
        SecurityEvent se = builder.createSecurityEvent(this.getClass().getName(), URI.create(ONE_PART_URI), LOG_MESSAGE, true);
        Assert.assertTrue(se.getActionUri().equals(ONE_PART_URI));
        Assert.assertTrue(se.getLogMessage().equals(LOG_MESSAGE));
        Assert.assertTrue(se.getUserEdOrg().equals(REALM_EDORG_STATEID));
        Assert.assertTrue(se.getTargetEdOrgList().contains(se.getUserEdOrg()));
    }

    // test setting the targetEdOrgList entity type and entity ids
    @Test
    public void testCreateSecurityEventFromEntityIds() {
        prepareTargetEdOrgMocks();
        String[] entityIds = {SECTION_ENTITY_ID, SECTION_ENTITY2_ID};
        SecurityEvent se = builder.createSecurityEvent(this.getClass().getName(), URI.create(ONE_PART_URI), LOG_MESSAGE, null, EntityNames.SECTION, entityIds);
        Assert.assertTrue(se.getActionUri().equals(ONE_PART_URI));
        Assert.assertTrue(se.getLogMessage().equals(LOG_MESSAGE));
        Assert.assertTrue(se.getUserEdOrg().equals(REALM_EDORG_STATEID));
        Assert.assertTrue(containsTheSame(se.getTargetEdOrgList(), Arrays.asList(EDORG_STATEID, EDORG2_STATEID)));
    }

    // test setting the targetEdOrgList entity type and entities
    @Test
    public void testCreateSecurityFromEventEntities() {
//        Entity student = createEntity(EntityNames.STUDENT, SECTION_ENTITY_ID, new HashMap<String, Object>());
//        createSecurityEvent(String loggingClass, URI requestUri, String slMessage,
//                String entityType, Set<Entity> entities)
        prepareTargetEdOrgMocks();
        Set<Entity> entities = new HashSet<Entity>();
        entities.add(section1);
        entities.add(section2);
        SecurityEvent se = builder.createSecurityEvent(this.getClass().getName(), URI.create(ONE_PART_URI), LOG_MESSAGE, null, EntityNames.SECTION, entities);
        Assert.assertTrue(se.getActionUri().equals(ONE_PART_URI));
        Assert.assertTrue(se.getLogMessage().equals(LOG_MESSAGE));
        Assert.assertTrue(se.getUserEdOrg().equals(REALM_EDORG_STATEID));
        Assert.assertTrue(containsTheSame(se.getTargetEdOrgList(), Arrays.asList(EDORG_STATEID, EDORG2_STATEID)));
    }

    private boolean containsTheSame(List<String> result, List<String> expected) {
        Set<String> resultSet = new HashSet<String>(result);
        Set<String> expectedSet = new HashSet<String>(expected);
        Set<String> diffSet = Sets.difference(expectedSet, resultSet);
        return diffSet.isEmpty();
    }

    private SecurityEvent testTargetEdOrgFromUriHelper(String uriString) {
        URI uri = URI.create(uriString);

        prepareTargetEdOrgMocks();
        return builder.createSecurityEvent(this.getClass().getName(), uri, LOG_MESSAGE, principal, null, null, null, false);
    }

    private void prepareTargetEdOrgMocks() {
        // single id in uri match
        Mockito.when(arbiter.findOwner(argThat(new BaseMatcher<Iterable<Entity>>() {

            @Override
            public boolean matches(Object item) {
                Iterable<Entity> received = (Iterable<Entity>) item;
                int count = 0;
                boolean matches = false;
                if (received == null) {
                    return false;
                }
                for (Entity entity : received) {
                    matches = entity.getType().equals(URI_ENTITY_TYPE) && entity.getEntityId().equals(SECTION_ENTITY_ID);
                    count++;
                }
                return (matches && count == 1);
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub
            }
        }), Mockito.eq(EntityNames.SECTION), Mockito.eq(false))).thenReturn(Arrays.asList(edOrg1));

        // two ids in uri match
        Mockito.when(arbiter.findOwner(argThat(new BaseMatcher<Iterable<Entity>>() {

            @Override
            public boolean matches(Object item) {
                Iterable<Entity> received = (Iterable<Entity>) item;
                int count = 0;
                boolean matches = false;
                if (received == null) {
                    return false;
                }
                for (Entity entity : received) {
                    matches = entity.getType().equals(URI_ENTITY_TYPE);
                    count++;
                }
                return (matches && count == 2);
            }

            @Override
            public void describeTo(Description description) {
                // TODO Auto-generated method stub
            }
        }), Mockito.eq(EntityNames.SECTION), Mockito.eq(false))).thenReturn(Arrays.asList(edOrg1, edOrg2));

        Mockito.when(repository.findById(Mockito.eq(EntityNames.SECTION), Mockito.matches(SECTION_ENTITY_ID))).thenReturn(section1);
        Mockito.when(repository.findById(Mockito.eq(EntityNames.SECTION), Mockito.matches(SECTION_ENTITY2_ID))).thenReturn(section2);
    }

    private Entity createEntity(String type, String id, Map<String, Object> body) {
        return new MongoEntity(type, id, body, new HashMap<String, Object>());
    }
}
