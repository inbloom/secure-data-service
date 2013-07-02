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


package org.slc.sli.api.security.resolve.impl;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import org.slc.sli.api.security.SLIPrincipal;
import org.slc.sli.api.security.context.resolver.EdOrgHelper;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.MongoEntity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * MongoUserLocatorTest
 *
 * @author tke
 *
 */
@Component
public class MongoUserLocatorTest {

    @InjectMocks
    MongoUserLocator locator = new MongoUserLocator();

    @Mock
    Repository<Entity> mockRepo;

    @Mock
    EdOrgHelper edorgHelper;

    private static final String tenant = "testTenant";

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(mockRepo.findOne(Matchers.eq(EntityNames.STUDENT), (NeutralQuery)Matchers.any(), Matchers.anyBoolean())).thenReturn(getEntity("student"));
        Mockito.when(mockRepo.findAll(Matchers.eq(EntityNames.STAFF), (NeutralQuery) Matchers.any())).thenReturn(Arrays.asList(getEntity("staff")));

    }

    @Test
    public void testStudentType() {
        SLIPrincipal principal = locator.locate(tenant, "testId", "student");
        assertTrue(principal.getEntity() != null);
        assertTrue(principal.getEntity().getType().equals(EntityNames.STUDENT));
    }

    @Test
    public void testStaffType() {
        Set<String> edorgs = new HashSet<String>();
        edorgs.add("testEdorg");
        Mockito.when(edorgHelper.locateDirectEdorgs((Entity)Matchers.any())).thenReturn(edorgs);

        SLIPrincipal principal = locator.locate(tenant, "testId", "staff");
        assertTrue(principal.getEntity() != null);
        assertTrue(principal.getEntity().getType().equals(EntityNames.STAFF));

        principal = locator.locate(tenant, "testId", "");
        assertTrue(principal.getEntity() != null);
        assertTrue(principal.getEntity().getType().equals(EntityNames.STAFF));

        principal = locator.locate(tenant, "testId", null);
        assertTrue(principal.getEntity() != null);
        assertTrue(principal.getEntity().getType().equals(EntityNames.STAFF));

    }

    @Test(expected = AccessDeniedException.class)
    public void testInvalidStaff() {
        Set<String> edorgs = new HashSet<String>();
        Mockito.when(edorgHelper.locateDirectEdorgs((Entity)Matchers.any())).thenReturn(edorgs);

        SLIPrincipal principal = locator.locate(tenant, "testId", "staff");
    }

    @Test
    public void testInvalidType() {
        SLIPrincipal principal = locator.locate(tenant, "testId", "nobody");
        assertTrue(principal.getEntity() != null);
        assertTrue(principal.getEntity().getType().equals("user"));
    }

    private Map<String, Object> getBody() {
        Map<String, Object> body = new HashMap<String, Object>();
        //really nothing need to be built at this moment
        return body;
    }

    private Entity getEntity(String type) {
        MongoEntity entity = new MongoEntity(type, getBody());
        return entity;
    }
}
