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

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
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
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.api.security.resolve.RolesToRightsResolver;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.slc.sli.domain.enums.Right;

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
    private static final String EDORG_COLLECTION = "educationOrganization";

    OauthMongoSessionManager sessionManager = new OauthMongoSessionManager();

    private RolesToRightsResolver resolver;
    private EdOrgHelper helper;

    private static final String APPLICATION_COLLECTIN = "application";

    Repository<Entity> repo;

    private final GrantedAuthority READ_PUBLIC = new GrantedAuthorityImpl(Right.READ_PUBLIC.name());
    private final GrantedAuthority READ_GENERAL = new GrantedAuthorityImpl(Right.READ_GENERAL.name());
    private final GrantedAuthority READ_RESTRICTED = new GrantedAuthorityImpl(Right.READ_RESTRICTED.name());
    private final GrantedAuthority AGGREGATE_READ = new GrantedAuthorityImpl(Right.AGGREGATE_READ.name());
    private final GrantedAuthority WRITE_PUBLIC = new GrantedAuthorityImpl(Right.WRITE_PUBLIC.name());
    private final GrantedAuthority WRITE_GENERAL = new GrantedAuthorityImpl(Right.WRITE_GENERAL.name());
    private final GrantedAuthority WRITE_RESTRICTED = new GrantedAuthorityImpl(Right.WRITE_RESTRICTED.name());
    private final GrantedAuthority AGGREGATE_WRITE = new GrantedAuthorityImpl(Right.AGGREGATE_WRITE.name());
    private final GrantedAuthority STAFF_CONTEXT = new GrantedAuthorityImpl(Right.STAFF_CONTEXT.name());
    private final GrantedAuthority TEACHER_CONTEXT = new GrantedAuthorityImpl(Right.TEACHER_CONTEXT.name());
    private final static String APP_CODE_VALUE = "c-82d4cca1-3654-47bb-8fb3-0d081f2e7b69";
    private final static String CLIENT_ID = "ke9Dgpo3uI";
    private final static String CLIENT_SECRETE = "uOoKXLWihlz39EEQ7Uoqqc7TeogsnQnDAUs3HWYFouZFG5sk";
    private final static String TOKEN = "c88ab6d7-117d-46aa-a207-2a58d1f72796";
    private final static String TENANT =  "Midgar";
    private final static String EXTERNAL_ID = "jdoe";

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        sessionManager = new OauthMongoSessionManager();

        repo = Mockito.mock(Repository.class);
        resolver = Mockito.mock(RolesToRightsResolver.class);
        helper = Mockito.mock(EdOrgHelper.class);
        UserLocator locator = Mockito.mock(UserLocator.class);
        ApplicationAuthorizationValidator appValidator = Mockito.mock(ApplicationAuthorizationValidator.class);

        // 1343219547095 --> date when test was made (7/25/2012 8:32am)
        // 1449935940000 --> date used by long-lived sessions
        Date date = new Date();
        long current = date.getTime();
        Long expirationForValidSession = current + 100000L;
        Long expirationForInvalidSession = current - 10000L;

        Map<String, Object> principal = new HashMap<String, Object>();
        principal.put("id", "jdoe@Midgar");
        principal.put("realm", "e5c12cb0-1bad-4606-a936-097b30bd47fe");
        principal.put("tenantId",TENANT);
        principal.put("name", "John Doe");
        principal.put("roles", Arrays.asList("Educator"));
        principal.put("externalId", EXTERNAL_ID);

        Mockito.when(locator.locate(Matchers.eq(TENANT), Matchers.eq(EXTERNAL_ID), Matchers.eq("staff"))).thenReturn(new SLIPrincipal());


        Map<String, Object> appCode = new HashMap<String, Object>();
        appCode.put("expiration", expirationForValidSession);
        appCode.put("value", APP_CODE_VALUE);

        List<Map<String, Object>> appSessionList = new ArrayList<Map<String, Object>>();
        Map<String, Object> appSession = new HashMap<String, Object>();
        appSession.put("token", TOKEN);
        appSession.put("verified", "false");
        appSession.put("state", "");
        appSession.put("code", appCode);
        appSession.put("clientId", CLIENT_ID);
        appSession.put("samlId", "sli-64a5e002-0ae8-4e5a-8f95-f46c14f354d4");

        appSessionList.add(appSession);

        Map<String, Object> validSession = new HashMap<String, Object>();
        validSession.put("tenantId", "Midgar");
        validSession.put("principal", principal);
        validSession.put("expiration", expirationForValidSession);
        validSession.put("hardLogout", expirationForValidSession);
        validSession.put("appSession", appSessionList);

        Map<String, Object> invalidSession = new HashMap<String, Object>();
        invalidSession.put("tenantId", "Midgar");
        invalidSession.put("principal", principal);
        invalidSession.put("expiration", expirationForInvalidSession);
        invalidSession.put("hardLogout", expirationForInvalidSession);
        invalidSession.put("appSession", appSession);

        Entity valid = new MongoEntity(SESSION_COLLECTION, "validSessionMongoId", validSession, new HashMap<String, Object>());
        Entity invalid = new MongoEntity(SESSION_COLLECTION, "invalidSessionMongoId", invalidSession, new HashMap<String, Object>());

        Mockito.when(repo.findAll(Mockito.eq(SESSION_COLLECTION), Mockito.any(NeutralQuery.class))).thenReturn(Arrays.asList(invalid));
        Mockito.when(repo.delete(Mockito.eq(SESSION_COLLECTION), Mockito.eq("invalidSessionMongoId"))).thenReturn(true);

        Mockito.when(repo.findOne(Matchers.eq(SESSION_COLLECTION),
                argThat(new BaseMatcher<NeutralQuery>() {

                    @Override
                    public boolean matches(Object arg0) {
                        NeutralQuery query = (NeutralQuery) arg0;
                        return query.getCriteria().contains(
                                new NeutralCriteria("appSession.code.value", "=", APP_CODE_VALUE));
                    }

                    @Override
                    public void describeTo(Description arg0) {
                    }
                }))).thenReturn(valid);

         Map<String, Object> applicationMap = new HashMap<String, Object>();
        applicationMap.put("client_id", CLIENT_ID);
        applicationMap.put("client_secret", CLIENT_SECRETE);

        Entity application = new MongoEntity(APPLICATION_COLLECTIN, "applicationId1", applicationMap, new HashMap<String, Object>());

        Mockito.when(repo.findOne(Matchers.eq("application"),
                argThat(new BaseMatcher<NeutralQuery>() {

                    @Override
                    public boolean matches(Object arg0) {
                        NeutralQuery query = (NeutralQuery) arg0;
                        return query.getCriteria().contains(
                                new NeutralCriteria("client_id", "=", CLIENT_ID))
                                && query.getCriteria().contains(
                                new NeutralCriteria("client_secret", "=", CLIENT_SECRETE));
                    }

                    @Override
                    public void describeTo(Description arg0) {
                    }
                }))).thenReturn(application);

        Mockito.when(appValidator.isAuthorizedForApp((Entity)Matchers.any(), (SLIPrincipal)Matchers.any())).thenReturn(true);

        sessionManager.setEntityRepository(repo);
        sessionManager.setLocator(locator);
        sessionManager.setAppValidator(appValidator);
    }

    @Test
    public void testSessionPurge() {
        Assert.assertEquals("Should return true", true, sessionManager.purgeExpiredSessions());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGenerateEdOrgRightsMap() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
                                                    InvocationTargetException, NoSuchFieldException {
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
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRoles1), eq(false), eq(false))).thenReturn(authorities1);
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRoles2), eq(false), eq(false))).thenReturn(authorities2);
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRoles3), eq(false), eq(false))).thenReturn(authorities3);

        Field field = OauthMongoSessionManager.class.getDeclaredField("resolver");
        field.setAccessible(true);
        field.set(sessionManager, resolver);

        Method method = OauthMongoSessionManager.class.getDeclaredMethod("generateEdOrgRightsMap", SLIPrincipal.class, boolean.class);
        method.setAccessible(true);
        Map<String, Collection<GrantedAuthority>> edOrgRights = (Map<String, Collection<GrantedAuthority>>) method.invoke(sessionManager, principal, false);

        Assert.assertEquals(3, edOrgRights.size());
        Assert.assertTrue(edOrgRights.get("edOrg1").equals(authorities1));
        Assert.assertTrue(edOrgRights.get("edOrg2").equals(authorities2));
        Assert.assertTrue(edOrgRights.get("edOrg3").equals(authorities3));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGenerateEdOrgRightsMapWithEmptyRoles() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
                                                                  InvocationTargetException, NoSuchFieldException {
        SLIPrincipal principal = new SLIPrincipal();
        principal.setTenantId(TENANT_ID);
        principal.setRealm(REALM_ID);
        principal.setAdminRealmAuthenticated(false);

        Field field = OauthMongoSessionManager.class.getDeclaredField("resolver");
        field.setAccessible(true);
        field.set(sessionManager, resolver);

        Method method = OauthMongoSessionManager.class.getDeclaredMethod("generateEdOrgRightsMap", SLIPrincipal.class, boolean.class);
        method.setAccessible(true);
        Map<String, Collection<GrantedAuthority>> edOrgRights = (Map<String, Collection<GrantedAuthority>>) method.invoke(sessionManager, principal, false);

        Assert.assertTrue(edOrgRights.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGenerateEdOrgRightsMapWithNullRoles() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
                                                                 InvocationTargetException, NoSuchFieldException {
        SLIPrincipal principal = new SLIPrincipal();
        principal.setTenantId(TENANT_ID);
        principal.setRealm(REALM_ID);
        principal.setAdminRealmAuthenticated(false);
        principal.setEdOrgRoles(null);

        Field field = OauthMongoSessionManager.class.getDeclaredField("resolver");
        field.setAccessible(true);
        field.set(sessionManager, resolver);

        Method method = OauthMongoSessionManager.class.getDeclaredMethod("generateEdOrgRightsMap", SLIPrincipal.class, boolean.class);
        method.setAccessible(true);
        Map<String, Collection<GrantedAuthority>> edOrgRights = (Map<String, Collection<GrantedAuthority>>) method.invoke(sessionManager, principal, false);

        Assert.assertTrue(edOrgRights.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGenerateEdOrgContextRightsCache() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
                                                             InvocationTargetException, NoSuchFieldException {
        SLIPrincipal principal = new SLIPrincipal();
        Map<String, List<String>> edOrgRoles = new HashMap<String, List<String>>();
        List<String> edOrgRole1 = new LinkedList<String>(Arrays.asList("IT Administrator"));
        List<String> edOrgRole2 = new LinkedList<String>(Arrays.asList("Counselor"));
        List<String> edOrgRole3 = new LinkedList<String>(Arrays.asList("Reading Aide"));
        List<String> edOrgRole4 = new LinkedList<String>(Arrays.asList("Math Teacher"));
        List<String> edOrgRole5 = new LinkedList<String>(Arrays.asList("Aggregate Viewer"));
        List<String> edOrgRoles1 = new LinkedList<String>(edOrgRole1);
        List<String> edOrgRoles2 = new LinkedList<String>(edOrgRole2);
        edOrgRoles2.addAll(edOrgRole4);
        List<String> edOrgRoles3 = new LinkedList<String>(edOrgRole3);
        edOrgRoles3.addAll(edOrgRole5);
        edOrgRoles.put("LEA1", edOrgRoles1);
        edOrgRoles.put("School1", edOrgRoles2);
        edOrgRoles.put("School2", edOrgRoles3);
        principal.setEdOrgRoles(edOrgRoles);
        principal.setTenantId(TENANT_ID);
        principal.setRealm(REALM_ID);
        principal.setAdminRealmAuthenticated(false);

        Set<GrantedAuthority> authorities1 = new HashSet<GrantedAuthority>(Arrays.asList(STAFF_CONTEXT));
        Set<GrantedAuthority> authorities2 = new HashSet<GrantedAuthority>(Arrays.asList(READ_RESTRICTED, WRITE_RESTRICTED));
        Set<GrantedAuthority> authorities3 = new HashSet<GrantedAuthority>(Arrays.asList(TEACHER_CONTEXT, READ_PUBLIC,
                AGGREGATE_READ, WRITE_PUBLIC, AGGREGATE_WRITE));
        Set<GrantedAuthority> authorities4 = new HashSet<GrantedAuthority>(Arrays.asList(READ_GENERAL));
        Set<GrantedAuthority> authorities5 = new HashSet<GrantedAuthority>(Arrays.asList(WRITE_GENERAL));
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRole1), eq(false), eq(false))).thenReturn(authorities1);
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRole2), eq(false), eq(false))).thenReturn(authorities2);
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRole3), eq(false), eq(false))).thenReturn(authorities3);
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRole4), eq(false), eq(false))).thenReturn(authorities4);
        Mockito.when(resolver.resolveRolesUnion(matches(TENANT_ID), matches(REALM_ID), eq(edOrgRole5), eq(false), eq(false))).thenReturn(authorities5);
        Field field1 = OauthMongoSessionManager.class.getDeclaredField("resolver");
        field1.setAccessible(true);
        field1.set(sessionManager, resolver);

        Entity school1 = new MongoEntity("educationOrganization", "School1", new HashMap<String, Object>(), new HashMap<String, Object>());
        Entity school2 = new MongoEntity("educationOrganization", "School2", new HashMap<String, Object>(), new HashMap<String, Object>());
        Entity lea1 = new MongoEntity("educationOrganization", "LEA1", new HashMap<String, Object>(), new HashMap<String, Object>());
        Mockito.when(repo.findById(Mockito.matches(EDORG_COLLECTION), Mockito.matches("School1"))).thenReturn(school1);
        Mockito.when(repo.findById(Mockito.matches(EDORG_COLLECTION), Mockito.matches("School2"))).thenReturn(school2);
        Mockito.when(repo.findById(Mockito.matches(EDORG_COLLECTION), Mockito.matches("LEA1"))).thenReturn(lea1);

        Mockito.when(helper.getParentEdOrgs(school1)).thenReturn(Arrays.asList("LEA1"));
        Mockito.when(helper.getParentEdOrgs(school2)).thenReturn(Arrays.asList("LEA1"));
        Mockito.when(helper.getParentEdOrgs(lea1)).thenReturn(new ArrayList<String>());
        Field field2 = OauthMongoSessionManager.class.getDeclaredField("helper");
        field2.setAccessible(true);
        field2.set(sessionManager, helper);

        Set<GrantedAuthority> authorities11 = new HashSet<GrantedAuthority>(authorities1);
        authorities11.addAll(authorities2);
        authorities11.addAll(authorities4);
        Set<GrantedAuthority> authorities12 = new HashSet<GrantedAuthority>(authorities1);
        authorities12.addAll(authorities3);
        authorities12.addAll(authorities5);

        Method method = OauthMongoSessionManager.class.getDeclaredMethod("generateEdOrgContextRightsCache", SLIPrincipal.class);
        method.setAccessible(true);
        SLIPrincipal.EdOrgContextRightsCache edOrgContextRights = (SLIPrincipal.EdOrgContextRightsCache) method.invoke(sessionManager, principal);

        Assert.assertEquals(3, edOrgContextRights.size());
        Assert.assertTrue(edOrgContextRights.get("School1").get(Right.STAFF_CONTEXT.name()).equals(authorities11));
        Assert.assertTrue(edOrgContextRights.get("School1").get(Right.TEACHER_CONTEXT.name()).isEmpty());
        Assert.assertTrue(edOrgContextRights.get("School2").get(Right.STAFF_CONTEXT.name()).equals(authorities12));
        Assert.assertTrue(edOrgContextRights.get("School2").get(Right.TEACHER_CONTEXT.name()).equals(authorities12));
        Assert.assertTrue(edOrgContextRights.get("LEA1").get(Right.STAFF_CONTEXT.name()).equals(authorities1));
        Assert.assertTrue(edOrgContextRights.get("LEA1").get(Right.TEACHER_CONTEXT.name()).isEmpty());
    }

    @Test
    public void testVerify() throws OAuthAccessException {
        Pair<String, String> credentials = new ImmutablePair<String, String>(CLIENT_ID, CLIENT_SECRETE);
        String token = sessionManager.verify(APP_CODE_VALUE, credentials);

        Assert.assertEquals(token, TOKEN);

    }
}
