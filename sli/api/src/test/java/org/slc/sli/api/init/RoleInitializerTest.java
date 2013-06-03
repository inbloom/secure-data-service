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

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.Repository;

/**
 * Simple test for RoleInitializer
 */
public class RoleInitializerTest {

    private RoleInitializer roleInitializer;
    private Repository<Entity> mockRepo;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        mockRepo = mock(Repository.class);
        Mockito.when(mockRepo.create(Mockito.anyString(), Mockito.any(Map.class))).thenReturn(new MongoEntity("entity", new HashMap<String, Object>()));
        roleInitializer = new RoleInitializer();
        roleInitializer.setRepository(mockRepo);
    }

    @Test
    public void testAllRolesCreated() throws Exception {
        assertTrue(roleInitializer.buildRoles("myRealmId") == 4);
    }
}
