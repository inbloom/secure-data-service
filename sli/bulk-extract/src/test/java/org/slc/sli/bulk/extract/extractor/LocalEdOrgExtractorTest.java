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
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.bulk.extract.BulkExtractMongoDA;
import org.slc.sli.bulk.extract.files.ExtractFile;
import org.slc.sli.bulk.extract.files.metadata.ManifestFile;
import org.slc.sli.bulk.extract.lea.CourseTranscriptExtractor;
import org.slc.sli.bulk.extract.lea.DisciplineExtractor;
import org.slc.sli.bulk.extract.lea.EdorgExtractor;
import org.slc.sli.bulk.extract.lea.EntityDatedExtract;
import org.slc.sli.bulk.extract.lea.EntityExtract;
import org.slc.sli.bulk.extract.lea.EntityToEdOrgCache;
import org.slc.sli.bulk.extract.lea.EntityToEdOrgDateCache;
import org.slc.sli.bulk.extract.lea.ExtractFileMap;
import org.slc.sli.bulk.extract.lea.ExtractorFactory;
import org.slc.sli.bulk.extract.lea.SectionEmbeddedDocsExtractor;
import org.slc.sli.bulk.extract.lea.StaffEdorgAssignmentExtractor;
import org.slc.sli.bulk.extract.lea.StudentCompetencyExtractor;
import org.slc.sli.bulk.extract.lea.StudentExtractor;
import org.slc.sli.bulk.extract.lea.StudentGradebookEntryExtractor;
import org.slc.sli.bulk.extract.lea.StudentSchoolAssociationExtractor;
import org.slc.sli.bulk.extract.lea.YearlyTranscriptExtractor;
import org.slc.sli.bulk.extract.util.EdOrgExtractHelper;
import org.slc.sli.bulk.extract.util.SecurityEventUtil;
import org.slc.sli.common.constants.EntityNames;
import org.slc.sli.common.constants.ParameterConstants;
import org.slc.sli.domain.Entity;
import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.domain.Repository;

/**
 * Tests LocalEdOrgExtractorTest
 *
 */
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LocalEdOrgExtractorTest {
    private Repository<Entity> repo;
    private Map<String, Object> body;
    private Entity mockEntity;
    private BulkExtractMongoDA mockMongo;
    private EntityExtractor entityExtractor;
    private EdOrgExtractHelper helper;
    private ExtractorFactory mockFactory;
    private ExtractFileMap mockExtractMap;
    private EntityExtract mockExtract;
    private EntityDatedExtract mockDatedExtract;
    private StaffEdorgAssignmentExtractor mockSeaExtractor;

    @Autowired
    private LocalEdOrgExtractor extractor;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        repo = Mockito.mock(Repository.class);
        mockFactory = Mockito.mock(ExtractorFactory.class);
        mockExtractMap = Mockito.mock(ExtractFileMap.class);
        mockExtract = Mockito.mock(EntityExtract.class);
        mockDatedExtract = Mockito.mock(EntityDatedExtract.class);
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
        helper = Mockito.mock(EdOrgExtractHelper.class);
        extractor.setHelper(helper);

        EdorgExtractor mockExtractor = Mockito.mock(EdorgExtractor.class);
        StudentExtractor mockStudent = Mockito.mock(StudentExtractor.class);
        StudentSchoolAssociationExtractor mockSsa = Mockito.mock(StudentSchoolAssociationExtractor.class);
        SectionEmbeddedDocsExtractor sectionEmbeddedDocsExtractor = Mockito.mock(SectionEmbeddedDocsExtractor.class);
        YearlyTranscriptExtractor yearlyTranscriptExtractor = Mockito.mock(YearlyTranscriptExtractor.class);
        CourseTranscriptExtractor courseTranscriptExtract = Mockito.mock(CourseTranscriptExtractor.class);
        DisciplineExtractor discipline = Mockito.mock(DisciplineExtractor.class);
        StudentCompetencyExtractor studentCompetencyExtractor = Mockito.mock(StudentCompetencyExtractor.class);
        StudentGradebookEntryExtractor studentGradebookEntryExtractor = Mockito.mock(StudentGradebookEntryExtractor.class);

        Mockito.when(sectionEmbeddedDocsExtractor.getStudentSectionAssociationDateCache()).thenReturn(new EntityToEdOrgDateCache());
        Mockito.when(mockFactory.buildEdorgExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                Mockito.eq(helper))).thenReturn(mockExtractor);

        Mockito.when(
                mockFactory.buildStudentExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockStudent);
        Mockito.when(
                mockFactory.buildAttendanceExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EntityToEdOrgDateCache.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockExtract);
        Mockito.when(
                mockFactory.buildStudentSchoolAssociationExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EntityToEdOrgDateCache.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockSsa);

        Mockito.when(
                mockFactory.buildStaffAssociationExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(
                mockSeaExtractor);

        Mockito.when(
                mockFactory.buildStaffExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockExtract);
        Mockito.when(
                mockFactory.buildTeacherSchoolExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockExtract);

        Mockito.when(
                mockFactory.buildStudentAssessmentExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockDatedExtract);

        Mockito.when(
                mockFactory.buildYearlyTranscriptExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(yearlyTranscriptExtractor);
        Mockito.when(
                mockFactory.buildParentExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockExtract);

        Mockito.when(
                mockFactory.buildStaffProgramAssociationExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockExtract);

        Mockito.when(
                mockFactory.buildStaffCohortAssociationExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(mockExtract);

        Mockito.when(mockFactory.buildSectionExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),Mockito.any(Repository.class),
                Mockito.any(EntityToEdOrgDateCache.class), Mockito.any(EntityToEdOrgCache.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(sectionEmbeddedDocsExtractor);

        Mockito.when(
                mockFactory.buildCourseTranscriptExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class), Mockito.any(Repository.class),
                        Mockito.any(EntityToEdOrgCache.class), Mockito.any(EntityToEdOrgDateCache.class))).thenReturn(courseTranscriptExtract);

        Mockito.when(
                mockFactory.buildStudentGradebookEntryExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class), Mockito.any(EdOrgExtractHelper.class))).thenReturn(studentGradebookEntryExtractor);

        Mockito.when(
                mockFactory.buildStudentCompetencyExtractor(Mockito.eq(entityExtractor),  Mockito.any(ExtractFileMap.class),
                        Mockito.any(Repository.class))).thenReturn(studentCompetencyExtractor);

        Mockito.when(mockFactory.buildDisciplineExtractor(Mockito.eq(entityExtractor), Mockito.any(ExtractFileMap.class), Mockito.any(Repository.class),
                Mockito.any(EntityToEdOrgCache.class), Mockito.any(EntityToEdOrgDateCache.class))).thenReturn(discipline);

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

    	extractor.execute("Midgar", tenantDir, new DateTime(), "sea");
        Mockito.verify(entityExtractor, Mockito.times(3)).extractEntities(Mockito.any(ExtractFile.class), Mockito.eq(EntityNames.EDUCATION_ORGANIZATION), null);
        Mockito.verify(entityExtractor, Mockito.times(3)).setExtractionQuery(Mockito.any(NeutralQuery.class));

    }

    @Ignore
    @Test
    public void testExecuteAgain() {
        File tenantDir = Mockito.mock(File.class);
        DateTime time = new DateTime();
        extractor.execute("Midgar", tenantDir, time, "sea");
        Mockito.verify(mockFactory, Mockito.times(1)).buildEdorgExtractor(entityExtractor, mockExtractMap, helper);
        Mockito.verify(mockExtractMap, Mockito.times(1)).archiveFiles();
        Mockito.verify(mockExtractMap, Mockito.times(1)).buildManifestFiles(time);
        Mockito.verify(mockExtractMap, Mockito.times(1)).closeFiles();
    }

    @Test
    public void testExecuteWithbuildLEAToExtractFile() throws IOException {
        mockExtractMap = null;
        extractor.setLeaToExtractMap(mockExtractMap);
        File tenantDir = Mockito.mock(File.class);
        String tenantPath = "bulk_extract";
        Mockito.when(tenantDir.getAbsolutePath()).thenReturn(tenantPath);
        String leaOne = "lea1";
        String leaTwo = "lea2";
        String leaThree = "lea2.1";
        String leaFour = "lea2.2";
        String appOne = "app1";
        String appTwo = "app2";
        Map<String, PublicKey> appPublicKeys = new HashMap<String, PublicKey>();
        PublicKey publicKey = Mockito.mock(PublicKey.class);
        appPublicKeys.put(appOne, publicKey);
        appPublicKeys.put(appTwo, publicKey);
        Mockito.when(mockMongo.getAppPublicKeys()).thenReturn(appPublicKeys);
        DateTime time = new DateTime();
        ManifestFile manifestFile = Mockito.mock(ManifestFile.class);

        ExtractFile extractFile1 = Mockito.mock(ExtractFile.class);
        Mockito.when(extractFile1.getManifestFile()).thenReturn(manifestFile);
        Map<String, File> archiveFiles1 = new HashMap<String, File>();
        archiveFiles1.put(appOne, new File(tenantPath + "/" + leaOne + "/" + appOne));
        archiveFiles1.put(appTwo, new File(tenantPath + "/" + leaOne + "/" + appTwo));
        Mockito.when(extractFile1.getArchiveFiles()).thenReturn(archiveFiles1);
        Mockito.when(mockFactory.buildLEAExtractFile(Mockito.eq(tenantPath), Mockito.eq(leaOne),
                Mockito.any(String.class), Mockito.eq(appPublicKeys), Mockito.any(SecurityEventUtil.class))).thenReturn(extractFile1);

        ExtractFile extractFile2 = Mockito.mock(ExtractFile.class);
        Mockito.when(extractFile2.getManifestFile()).thenReturn(manifestFile);
        Map<String, File> archiveFiles2 = new HashMap<String, File>();
        archiveFiles2.put(appOne, new File(tenantPath + "/" + leaTwo + "/" + appOne));
        archiveFiles2.put(appTwo, new File(tenantPath + "/" + leaTwo + "/" + appTwo));
        Mockito.when(extractFile2.getArchiveFiles()).thenReturn(archiveFiles2);
        Mockito.when(mockFactory.buildLEAExtractFile(Mockito.eq(tenantPath), Mockito.eq(leaTwo),
                Mockito.any(String.class), Mockito.eq(appPublicKeys), Mockito.any(SecurityEventUtil.class))).thenReturn(extractFile2);

        ExtractFile extractFile3 = Mockito.mock(ExtractFile.class);
        Mockito.when(extractFile3.getManifestFile()).thenReturn(manifestFile);
        Map<String, File> archiveFiles3 = new HashMap<String, File>();
        archiveFiles3.put(appOne, new File(tenantPath + "/" + leaThree + "/" + appOne));
        Mockito.when(extractFile3.getArchiveFiles()).thenReturn(archiveFiles3);
        Mockito.when(mockFactory.buildLEAExtractFile(Mockito.eq(tenantPath), Mockito.eq(leaThree),
                Mockito.any(String.class), Mockito.eq(appPublicKeys), Mockito.any(SecurityEventUtil.class))).thenReturn(extractFile3);

        ExtractFile extractFile4 = Mockito.mock(ExtractFile.class);
        Mockito.when(extractFile4.getManifestFile()).thenReturn(manifestFile);
        Map<String, File> archiveFiles4 = new HashMap<String, File>();
        archiveFiles4.put(appTwo, new File(tenantPath + "/" + leaFour + "/" + appTwo));
        Mockito.when(extractFile4.getArchiveFiles()).thenReturn(archiveFiles4);
        Mockito.when(mockFactory.buildLEAExtractFile(Mockito.eq(tenantPath), Mockito.eq(leaFour),
                Mockito.any(String.class), Mockito.eq(appPublicKeys), Mockito.any(SecurityEventUtil.class))).thenReturn(extractFile4);

        Mockito.when(helper.getBulkExtractEdOrgs("sea")).thenReturn(new HashSet<String>(Arrays.asList(leaOne, leaTwo, leaThree, leaFour)));
        Mockito.when(helper.getChildEdOrgs(Mockito.eq(Arrays.asList(leaOne)))).thenReturn(new HashSet<String>());
        Mockito.when(helper.getChildEdOrgs(Mockito.eq(Arrays.asList(leaTwo)))).thenReturn(new HashSet<String>(Arrays.asList(leaThree, leaFour)));

        extractor.execute("Midgar", tenantDir, time, "sea");

        Mockito.verify(helper, Mockito.times(3)).getBulkExtractEdOrgs("sea");
        Mockito.verify(helper, Mockito.times(4)).getChildEdOrgs(Mockito.any(List.class));
        Mockito.verify(mockMongo, Mockito.times(6)).updateDBRecord(Mockito.eq("Midgar"), Mockito.any(String.class), Mockito.any(String.class),
                Mockito.any(Date.class), Mockito.eq(false), Mockito.any(String.class), Mockito.eq(false));

        // Verify database was populated correctly.
        Mockito.verify(mockMongo, Mockito.times(1)).updateDBRecord(Mockito.eq("Midgar"), Mockito.contains(tenantPath + "/" + leaOne + "/" + appOne),
                Mockito.eq(appOne), Mockito.any(Date.class), Mockito.eq(false), Mockito.eq(leaOne), Mockito.eq(false));
        Mockito.verify(mockMongo, Mockito.times(1)).updateDBRecord(Mockito.eq("Midgar"), Mockito.contains(tenantPath + "/" + leaOne + "/" + appTwo),
                Mockito.eq(appTwo), Mockito.any(Date.class), Mockito.eq(false), Mockito.eq(leaOne), Mockito.eq(false));
        Mockito.verify(mockMongo, Mockito.times(1)).updateDBRecord(Mockito.eq("Midgar"), Mockito.contains(tenantPath + "/" + leaTwo + "/" + appOne),
                Mockito.eq(appOne), Mockito.any(Date.class), Mockito.eq(false), Mockito.eq(leaTwo), Mockito.eq(false));
        Mockito.verify(mockMongo, Mockito.times(1)).updateDBRecord(Mockito.eq("Midgar"), Mockito.contains(tenantPath + "/" + leaTwo + "/" + appTwo),
                Mockito.eq(appTwo), Mockito.any(Date.class), Mockito.eq(false), Mockito.eq(leaTwo), Mockito.eq(false));
        Mockito.verify(mockMongo, Mockito.times(1)).updateDBRecord(Mockito.eq("Midgar"), Mockito.contains(tenantPath + "/" + leaThree + "/" + appOne),
                Mockito.eq(appOne), Mockito.any(Date.class), Mockito.eq(false), Mockito.eq(leaThree), Mockito.eq(false));
        Mockito.verify(mockMongo, Mockito.times(1)).updateDBRecord(Mockito.eq("Midgar"), Mockito.contains(tenantPath + "/" + leaFour + "/" + appTwo),
                Mockito.eq(appTwo), Mockito.any(Date.class), Mockito.eq(false), Mockito.eq(leaFour), Mockito.eq(false));

        File testDir = new File(tenantPath);
        FileUtils.deleteQuietly(testDir);
    }

}
