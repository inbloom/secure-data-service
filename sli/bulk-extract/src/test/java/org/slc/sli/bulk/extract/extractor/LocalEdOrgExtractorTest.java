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

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.lea.EdorgExtractor;
import org.slc.sli.bulk.extract.lea.EntityExtract;
import org.slc.sli.bulk.extract.lea.EntityToLeaCache;
import org.slc.sli.bulk.extract.lea.LEAExtractFileMap;
import org.slc.sli.bulk.extract.lea.LEAExtractorFactory;
import org.slc.sli.bulk.extract.lea.StaffEdorgAssignmentExtractor;
import org.slc.sli.bulk.extract.lea.StudentExtractor;
import org.slc.sli.bulk.extract.lea.StudentSchoolAssociationExtractor;
import org.slc.sli.bulk.extract.util.LocalEdOrgExtractHelper;
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
    private LocalEdOrgExtractHelper helper;
    private LEAExtractorFactory mockFactory;
    private LEAExtractFileMap mockExtractMap;
    private EntityExtract mockExtract;
    private StaffEdorgAssignmentExtractor mockSeaExtractor;

    @Autowired
    private LocalEdOrgExtractor extractor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        repo = Mockito.mock(Repository.class);
        mockFactory = Mockito.mock(LEAExtractorFactory.class);
        mockExtractMap = Mockito.mock(LEAExtractFileMap.class);
        mockExtract = Mockito.mock(EntityExtract.class);
        extractor.setLeaToExtractMap(mockExtractMap);
        extractor.setFactory(mockFactory);
        extractor.setRepository(repo);
        body = new HashMap<String, Object>();
        body.put("isBulkExtract", true);
        mockEntity = Mockito.mock(Entity.class);
        mockMongo = Mockito.mock(BulkExtractMongoDA.class);
        extractor.setBulkExtractMongoDA(mockMongo);
        Mockito.when(mockEntity.getBody()).thenReturn(body);
        entityExtractor = Mockito.mock(EntityExtractor.class);
        mockSeaExtractor = Mockito.mock(StaffEdorgAssignmentExtractor.class);
        extractor.setEntityExtractor(entityExtractor);
        helper = Mockito.mock(LocalEdOrgExtractHelper.class);
        extractor.setHelper(helper);
        
        EdorgExtractor mockExtractor = Mockito.mock(EdorgExtractor.class);
        StudentExtractor mockStudent = Mockito.mock(StudentExtractor.class);
        StudentSchoolAssociationExtractor mockSsa = Mockito.mock(StudentSchoolAssociationExtractor.class);

        Mockito.when(mockFactory.buildEdorgExtractor(entityExtractor, mockExtractMap)).thenReturn(mockExtractor);
        Mockito.when(
                mockFactory.buildStudentExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class))).thenReturn(mockStudent);
        Mockito.when(
                mockFactory.buildAttendanceExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class), Mockito.any(EntityToLeaCache.class))).thenReturn(mockExtract);
        Mockito.when(
                mockFactory.buildStudentSchoolAssociationExractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class), Mockito.any(EntityToLeaCache.class))).thenReturn(mockSsa);
        
        Mockito.when(
                mockFactory.buildStaffAssociationExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class))).thenReturn(
                mockSeaExtractor);
        
        Mockito.when(
                mockFactory.buildStaffExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class))).thenReturn(mockExtract);
        Mockito.when(
                mockFactory.buildTeacherSchoolExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class))).thenReturn(mockExtract);
        
        Mockito.when(
                mockFactory.buildStudentAssessmentExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class))).thenReturn(mockExtract);
        
        Mockito.when(
                mockFactory.buildYearlyTranscriptExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class))).thenReturn(mockExtract);
        Mockito.when(
                mockFactory.buildParentExtractor(Mockito.eq(entityExtractor), Mockito.eq(mockExtractMap),
                        Mockito.any(Repository.class))).thenReturn(mockExtract);


    }
    
    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        repo = null;
    }


    @Test
    @Ignore
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

        Mockito.when(helper.getBulkExtractLEAs()).thenReturn(new HashSet<String>(Arrays.asList("edorg1", "edorg2")));

    	extractor.execute("Midgar", tenantDir, new DateTime());
        Mockito.verify(entityExtractor, Mockito.times(3)).extractEntities(Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.EDUCATION_ORGANIZATION));
        Mockito.verify(entityExtractor, Mockito.times(3)).setExtractionQuery(Mockito.any(Query.class));

    }
    
    @Test
    public void testExecuteAgain() {
        File tenantDir = Mockito.mock(File.class);
        DateTime time = new DateTime();
        extractor.execute("Midgar", tenantDir, time);
        Mockito.verify(mockFactory, Mockito.times(1)).buildEdorgExtractor(entityExtractor, mockExtractMap);
        Mockito.verify(mockExtractMap, Mockito.times(1)).archiveFiles();
        Mockito.verify(mockExtractMap, Mockito.times(1)).buildManifestFiles(time);
        Mockito.verify(mockExtractMap, Mockito.times(1)).closeFiles();
    }

}
