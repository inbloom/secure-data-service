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

package org.slc.sli.api.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.api.constants.EntityNames;
import org.slc.sli.api.util.SecurityUtil.SecurityUtilProxy;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Tests for SuperAdminService
 */
public class SuperAdminServiceTest {

    @InjectMocks
    SuperAdminService service = new SuperAdminService();

    @Mock
    Repository<Entity> repo;

    @Mock
    SecurityUtilProxy secUtil;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllowedEdOrgs() {
        Mockito.when(secUtil.getTenantId()).thenReturn("TENANT");

        Entity user = Mockito.mock(Entity.class);
        HashMap<String, Object> body = new HashMap<String, Object>();
        body.put("stateOrganizationId", "ID");
        body.put("organizationCategories", Arrays.asList("State Education Agency"));
        Mockito.when(user.getBody()).thenReturn(body);

        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(Arrays.asList(user));

        Set<String> edOrgs = service.getAllowedEdOrgs(null, null);
        Assert.assertEquals(new HashSet<String>(Arrays.asList("ID")), edOrgs);

        Entity edOrg = Mockito.mock(Entity.class);
        Mockito.when(edOrg.getEntityId()).thenReturn("EDORGID");
        Mockito.when(repo.findOne(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.any(NeutralQuery.class)))
                .thenReturn(edOrg);
        edOrgs = service.getAllowedEdOrgs("TENANT", null);
    }

}
