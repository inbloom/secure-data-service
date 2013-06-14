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

import java.util.*;

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
import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.resolve.UserLocator;
import org.slc.sli.domain.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.OauthMongoSessionManager;
import org.slc.sli.api.test.WebContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import static org.mockito.Matchers.argThat;

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
    private static final String APPLICATION_COLLECTIN = "application";

    OauthMongoSessionManager sessionManager;
    Repository<Entity> repo;

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

        Entity staffEntity = new MongoEntity(SESSION_COLLECTION, "staffEntityId", new HashMap<String, Object>(), new HashMap<String, Object>());
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

    @Test
    public void testVerify() throws OAuthAccessException {
        Pair<String, String> credentials = new ImmutablePair<String, String>(CLIENT_ID, CLIENT_SECRETE);
        String token = sessionManager.verify(APP_CODE_VALUE, credentials);

        Assert.assertEquals(token, TOKEN);

    }
}
