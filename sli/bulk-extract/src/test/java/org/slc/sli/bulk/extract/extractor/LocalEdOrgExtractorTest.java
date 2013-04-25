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

/**
 * 
 */
package org.slc.sli.bulk.extract.extractor;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private BulkExtractMongoDA mockMongo;
    private EntityExtractor entityExtractor;
    
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
        mockMongo = Mockito.mock(BulkExtractMongoDA.class);
        extractor.setBulkExtractMongoDA(mockMongo);
        Mockito.when(mockEntity.getBody()).thenReturn(body);
        entityExtractor = Mockito.mock(EntityExtractor.class);
        extractor.setEntityExtractor(entityExtractor);
    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        repo = null;
    }
    
    @Test
    public void testExecute() {
    	File tenantDir = Mockito.mock(File.class);
    	
        Map<String, Object> registration = new HashMap<String, Object>();
        registration.put("status", "APPROVED");
        body.put("registration", registration);
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(mockEntity));
        body.put("edorgs", Arrays.asList("lea-one", "lea-two", "lea-three"));
        Mockito.when(repo.findAll(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(mockEntity));
        Entity edOrgOne = Mockito.mock(Entity.class);
        Mockito.when(edOrgOne.getEntityId()).thenReturn("edorg1");
        Entity edOrgTwo = Mockito.mock(Entity.class);
        Mockito.when(edOrgTwo.getEntityId()).thenReturn("edorg2");
        NeutralQuery baseQuery1 = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, Arrays.asList("lea-one")));
        NeutralQuery baseQuery2 = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, Arrays.asList("lea-two")));
        NeutralQuery baseQuery3 = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, Arrays.asList("lea-three")));

        NeutralQuery childQuery = new NeutralQuery(new NeutralCriteria(ParameterConstants.PARENT_EDUCATION_AGENCY_REFERENCE,
                NeutralCriteria.CRITERIA_IN, new HashSet<String>(Arrays.asList("edorg1", "edorg2"))));

        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.eq(baseQuery1))).thenReturn(new ArrayList<Entity>());
        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.eq(baseQuery2))).thenReturn(Arrays.asList(edOrgOne, edOrgTwo));
        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.eq(baseQuery3))).thenReturn(new ArrayList<Entity>());
        Mockito.when(repo.findAll(Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), Mockito.eq(childQuery))).thenReturn(new ArrayList<Entity>());

    	extractor.execute("Midgar", tenantDir, new DateTime());
        Mockito.verify(entityExtractor, Mockito.times(3)).extractEntities(Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.EDUCATION_ORGANIZATION));
        Mockito.verify(entityExtractor, Mockito.times(3)).setExtractionQuery(Mockito.any(Query.class));

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
    public void testGetBulkExtractLEAsPerApp() {
        // No LEAs
        body.put("edorgs", new ArrayList<String>());
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                new ArrayList<Entity>());
        Mockito.when(repo.findAll(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(
        		new ArrayList<Entity>());
        Assert.assertTrue(extractor.getBulkExtractLEAsPerApp().size() == 0);
        body.put("edorgs", Arrays.asList("one", "two", "three"));
        Mockito.when(repo.findAll(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(mockEntity));
        Assert.assertTrue(extractor.getBulkExtractLEAsPerApp().size() == 1);
        Assert.assertTrue(extractor.getBulkExtractLEAsPerApp().get(null).size() == 3);

    }
    
    @Test
    public void testLeaToApps() {
        Map<String, Object> registration = new HashMap<String, Object>();
        registration.put("status", "APPROVED");
        body.put("registration", registration);
        Mockito.when(repo.findAll(Mockito.eq("application"), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(mockEntity));
        body.put("edorgs", Arrays.asList("one"));
        body.put("applicationId", "app1");
        Mockito.when(repo.findAll(Mockito.eq("applicationAuthorization"), Mockito.any(NeutralQuery.class))).thenReturn(
                Arrays.asList(mockEntity));

    	
    	Map<String, Set<String>> result = extractor.leaToApps();
    	Assert.assertEquals(result.size(), 1);
    	Assert.assertEquals(result.get("one").size(), 1);
    	Assert.assertTrue(result.get("one").contains("app1"));
    }

}
