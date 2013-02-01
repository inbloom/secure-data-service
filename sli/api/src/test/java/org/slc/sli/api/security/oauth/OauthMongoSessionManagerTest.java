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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import org.slc.sli.api.security.OauthMongoSessionManager;
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

    OauthMongoSessionManager sessionManager;
    Repository<Entity> repo;

    @SuppressWarnings("unchecked")
    @Before
    public void setup() {
        sessionManager = new OauthMongoSessionManager();
        repo = Mockito.mock(Repository.class);

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
}
