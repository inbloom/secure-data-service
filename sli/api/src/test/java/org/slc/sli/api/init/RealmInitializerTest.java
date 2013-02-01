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

package org.slc.sli.api.init;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * 
 *
 */
public class RealmInitializerTest {
    
    @InjectMocks
    private RealmInitializer realmInit;
        
    @Mock
    private Repository<Entity> mockRepo;
    
    @Before
    public void setUp() throws Exception {
        realmInit = new RealmInitializer();
        MockitoAnnotations.initMocks(this);
    }
    
    @Test
    @SuppressWarnings("unchecked")
    public void testRealmNotExist() throws Exception {
        
        // verify that the code attempts to insert a new realm when no existing realm is present
        Mockito.when(mockRepo.findOne(Mockito.eq("realm"), Mockito.any(NeutralQuery.class))).thenReturn(null);
        final AtomicBoolean update = new AtomicBoolean(false);
        Mockito.when(mockRepo.create(Mockito.anyString(), Mockito.any(Map.class))).thenAnswer(new Answer<Boolean>() {
            
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                update.set(true);
                return true;
            }
            
        });
        realmInit.bootstrap();
        assertTrue("Repo was updated with new realm", update.get());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testOutdatedRealm() throws Exception {
        
        // verify that the code attempts to insert a new realm if the existing one needs to be
        // modified
        Map body = realmInit.createAdminRealmBody();
        body.put("name", "New name");
        Entity existingRealm = new MongoEntity("realm", body);
        Mockito.when(mockRepo.findOne(Mockito.eq("realm"), Mockito.any(NeutralQuery.class))).thenReturn(existingRealm);
        final AtomicBoolean update = new AtomicBoolean(false);
        Mockito.when(mockRepo.update(Mockito.anyString(), Mockito.any(Entity.class))).thenAnswer(new Answer<Boolean>() {
            
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                update.set(true);
                return true;
            }
            
        });
        realmInit.bootstrap();
        assertTrue("Existing realm was updated", update.get());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testRealmUnchanged() throws Exception {
        NeutralQuery adminQuery = new NeutralQuery(new NeutralCriteria("uniqueIdentifier",
                NeutralCriteria.OPERATOR_EQUAL, RealmInitializer.ADMIN_REALM_ID));
        
        NeutralQuery developerQuery = new NeutralQuery(new NeutralCriteria("uniqueIdentifier",
                NeutralCriteria.OPERATOR_EQUAL, null)); 
        // verify that the code doesn't attempt to update the realm if the existing one hasn't been
        // modified
        Map body = realmInit.createAdminRealmBody();
        Entity existingRealm = new MongoEntity("realm", body);
        Map devbody = realmInit.createDeveloperRealmBody();
        Entity existingDevRealm = new MongoEntity("realm", devbody);
        
        Mockito.when(mockRepo.findOne(Mockito.eq("realm"), Mockito.eq(adminQuery))).thenReturn(existingRealm);
        Mockito.when(mockRepo.findOne(Mockito.eq("realm"), Mockito.eq(developerQuery))).thenReturn(existingDevRealm);
        
        final AtomicBoolean update = new AtomicBoolean(false);
        Mockito.when(mockRepo.update(Mockito.anyString(), Mockito.any(Entity.class))).thenAnswer(new Answer<Boolean>() {
            
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                update.set(true);
                return true;
            }
            
        });
        realmInit.bootstrap();
        assertFalse("Existing realm was not touched", update.get());
    }
    
}
