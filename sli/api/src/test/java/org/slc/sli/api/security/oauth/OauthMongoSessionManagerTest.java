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

package org.slc.sli.api.security.oauth;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.OauthMongoSessionManager;
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Tests oauth with mongo.
 *
 *
 * @author kmyers
 *
 */
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
@TestExecutionListeners({ WebContextTestExecutionListener.class, DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class })
public class OauthMongoSessionManagerTest {

    private static final String SESSION_COLLECTION = "userSession";
    private static final String TENANT_ID = "Midgar";
    private static final String REALM_ID = "Middle Earth";

    OauthMongoSessionManager sessionManager = new OauthMongoSessionManager();

    private RolesToRightsResolver resolver;

    Repository<Entity> repo;

    private final GrantedAuthority READ_PUBLIC = new GrantedAuthorityImpl("READ_PUBLIC");
    private final GrantedAuthority READ_GENERAL = new GrantedAuthorityImpl("READ_GENERAL");
    private final GrantedAuthority READ_RESTRICTED = new GrantedAuthorityImpl("READ_RESTRICTED");
    private final GrantedAuthority AGGREGATE_READ = new GrantedAuthorityImpl("AGGREGATE_READ");
    private final GrantedAuthority WRITE_PUBLIC = new GrantedAuthorityImpl("WRITE_PUBLIC");
    private final GrantedAuthority WRITE_GENERAL = new GrantedAuthorityImpl("WRITE_GENERAL");
    private final GrantedAuthority WRITE_RESTRICTED = new GrantedAuthorityImpl("WRITE_RESTRICTED");
    private final GrantedAuthority AGGREGATE_WRITE = new GrantedAuthorityImpl("AGGREGATE_WRITE");

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        sessionManager = new OauthMongoSessionManager();

        repo = Mockito.mock(Repository.class);
        resolver = Mockito.mock(RolesToRightsResolver.class);

        // 1343219547095 --> date when test was made (7/25/2012 8:32am)
        // 1449935940000 --> date used by long-lived sessions
        Long expirationForValidSession = new Long(1449935940000L);
        Long expirationForInvalidSession = new Long(1343219547095L);

        Map<String, Object> principal = new HashMap<String, Object>();
        principal.put("id", "jdoe@Midgar");
        principal.put("realm", "e5c12cb0-1bad-4606-a936-097b30bd47fe");
        principal.put("tenantId", "Midgar");
        principal.put("name", "John Doe");
        principal.put("roles", Arrays.asList("Educator"));
        principal.put("externalId", "jdoe");

        Map<String, Object> appCode = new HashMap<String, Object>();
        appCode.put("expiration", expirationForValidSession);
        appCode.put("value", "c-82d4cca1-3654-47bb-8fb3-0d081f2e7b69");

        Map<String, Object> appSession = new HashMap<String, Object>();
        principal.put("token", "c88ab6d7-117d-46aa-a207-2a58d1f72796");
        principal.put("verified", "true");
        principal.put("state", "");
        principal.put("code", appCode);
        principal.put("clientId", "ke9Dgpo3uI");
        principal.put("samlId", "sli-64a5e002-0ae8-4e5a-8f95-f46c14f354d4");

        Map<String, Object> validSession = new HashMap<String, Object>();
        validSession.put("tenantId", "Midgar");
        validSession.put("principal", principal);
        validSession.put("expiration", expirationForValidSession);
        validSession.put("hardLogout", expirationForValidSession);
        validSession.put("appSession", appSession);

        Map<String, Object> invalidSession = new HashMap<String, Object>();
        invalidSession.put("tenantId", "Midgar");
        invalidSession.put("principal", principal);
        invalidSession.put("expiration", expirationForInvalidSession);
        invalidSession.put("hardLogout", expirationForInvalidSession);
        invalidSession.put("appSession", appSession);

        Entity invalid = new MongoEntity(SESSION_COLLECTION, "invalidSessionMongoId", invalidSession, new HashMap<String, Object>());

        Mockito.when(repo.findAll(Mockito.eq(SESSION_COLLECTION), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(invalid));
        Mockito.when(repo.delete(Mockito.eq(SESSION_COLLECTION), Mockito.eq("invalidSessionMongoId"))).thenReturn(true);

        sessionManager.setEntityRepository(repo);
    }

    @Test
    public void testSessionPurge() {
        Assert.assertEquals("Should return true", true, sessionManager.purgeExpiredSessions());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAddEdOrgRightsToPrincipal() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        SLIPrincipal principal = new SLIPrincipal();
        Map<String, List<String>> edOrgRoles = new HashMap<String, List<String>>();
        List<String> edOrgRoles1 = new LinkedList<String>(Arrays.asList("Gym Teacher", "Science Teacher", "Principal"));
        List<String> edOrgRoles2 = new LinkedList<String>(Arrays.asList("Counselor", "Gym Teacher", "Principal"));
        List<String> edOrgRoles3 = new LinkedList<String>(Arrays.asList("Science Teacher", "Reading Aide"));
        edOrgRoles.put("edOrg1", edOrgRoles1);
        edOrgRoles.put("edOrg2", edOrgRoles2);
        edOrgRoles.put("edOrg3", edOrgRoles3);
        principal.setEdOrgRoles(edOrgRoles);
        principal.setTenantId(TENANT_ID);
        principal.setRealm(REALM_ID);
        principal.setAdminRealmAuthenticated(false);

        Set<GrantedAuthority> authorities1 = new HashSet<GrantedAuthority>(Arrays.asList(READ_PUBLIC,
                READ_GENERAL, WRITE_GENERAL));
        Set<GrantedAuthority> authorities2 = new HashSet<GrantedAuthority>(Arrays.asList(READ_RESTRICTED,
                WRITE_RESTRICTED));
        Set<GrantedAuthority> authorities3 = new HashSet<GrantedAuthority>(Arrays.asList(READ_PUBLIC,
                AGGREGATE_READ, WRITE_PUBLIC, AGGREGATE_WRITE));
        Mockito.when(resolver.resolveRoles(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRoles1), eq(false), eq(false))).thenReturn(authorities1);
        Mockito.when(resolver.resolveRoles(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRoles2), eq(false), eq(false))).thenReturn(authorities2);
        Mockito.when(resolver.resolveRoles(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRoles3), eq(false), eq(false))).thenReturn(authorities3);

        Assert.assertTrue(principal.getEdOrgRights().isEmpty());

        Field field = OauthMongoSessionManager.class.getDeclaredField("resolver");
        field.setAccessible(true);
        field.set(sessionManager, resolver);

        Method method = OauthMongoSessionManager.class.getDeclaredMethod("addEdOrgRightsToPrincipal", SLIPrincipal.class);
        method.setAccessible(true);
        method.invoke(sessionManager, principal);

        Assert.assertEquals(3, principal.getEdOrgRights().size());
        Assert.assertTrue(principal.getEdOrgRights().get("edOrg1").equals(authorities1));
        Assert.assertTrue(principal.getEdOrgRights().get("edOrg2").equals(authorities2));
        Assert.assertTrue(principal.getEdOrgRights().get("edOrg3").equals(authorities3));
    }

}
