/*
 * Copyright 2012 Shared Learning Collaborative, LLC
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

/**
 * 
 */
package org.slc.sli.bulk.extract.extractor;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Tests LocalEdOrgExtractorTest
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LocalEdOrgExtractorTest {
    private Repository<Entity> repo;
    private Map<String, Object> body;
    private Entity mockEntity;
    
    @Autowired
    private LocalEdOrgExtractor extractor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        repo = Mockito.mock(Repository.class);
        extractor.setRepository(repo);
        body = new HashMap<String, Object>();
        body.put("isBulkExtract", true);
        mockEntity = Mockito.mock(Entity.class);
        Mockito.when(mockEntity.getBody()).thenReturn(body);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        repo = null;
    }
    
    @Test
    public void testBuildEdorgCache() {
        
    }
    
    @Test
    public void testGetBulkExtractApps() {
        Map<String, Object> registration = new HashMap<String, Object>();
        registration.put("status", "APPROVED");
        body.put("registration", registration);
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(mockEntity));
        Assert.assertTrue(extractor.getBulkExtractApps().size() == 1);
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                new ArrayList<Entity>());
        Assert.assertTrue(extractor.getBulkExtractApps().size() == 0);
    }
    
    @Test
    public void getBulkExtractLEAs() {
        // No LEAs
        body.put("edorgs", new ArrayList<String>());
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                new ArrayList<Entity>());
        Mockito.when(repo.findAll(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(mockEntity));
        Assert.assertTrue(extractor.getAllLEAs(extractor.getBulkExtractLEAsPerApp()).size() == 0);
        body.put("edorgs", Arrays.asList("one", "two", "three"));
        Assert.assertTrue(extractor.getAllLEAs(extractor.getBulkExtractLEAsPerApp()).size() == 3);
    }

}
