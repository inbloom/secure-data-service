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

package org.slc.sli.ingestion.transformation;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.domain.Entity;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.transformation.normalization.EntityConfigFactory;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.util.EntityTestUtils;
import org.slc.sli.validation.EntityValidator;

/**
 * Unit Test for SmooksEdFi2SLITransformer
 *
 * @author okrook
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/transformation2-context.xml" })
public class SmooksEdFi2SLITransformerTest {

    @Autowired
    SmooksEdFi2SLITransformer transformer;

    @Autowired
    private EntityValidator validator;

    private static final String STUDENT_ID = "303A1";
    private static final String TENANT_ID = "SLI";
    private static final String TENANT_ID_FIELD = "tenantId";
    private static final String EXTERNAL_ID_FIELD = "externalId";
    private static final String ASSESSMENT_TITLE = "assessmentTitle";
    private static final String ASSESSMENT_FAMILY_ID = "AF-1";

    @Test
    public void testDirectMapping() {
        NeutralRecord directlyMapped = new NeutralRecord();
        directlyMapped.setRecordType("directEntity");
        directlyMapped.setAttributeField("field2", "Test String");
        ReportStats reportStats = new SimpleReportStats();

        List<? extends Entity> result = transformer.transform(directlyMapped,
                new DummyMessageReport(), reportStats);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("Test String", result.get(0).getBody().get("field1"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAssessmentMapping() {
        NeutralRecord assessment = createAssessmentNeutralRecord(false);

        ReportStats reportStats = new SimpleReportStats();

        List<? extends Entity> result = transformer.transform(assessment, new DummyMessageReport(),
                reportStats);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        Assert.assertEquals("assessmentTitle", result.get(0).getBody().get("assessmentTitle"));
        Assert.assertEquals(ASSESSMENT_FAMILY_ID,
                result.get(0).getBody().get("assessmentFamilyReference"));

        List<Map<String, Object>> assessmentIDCodeList = (List<Map<String, Object>>) result.get(0)
                .getBody().get("assessmentIdentificationCode");
        Assert.assertNotNull(assessmentIDCodeList);
        Assert.assertEquals(2, assessmentIDCodeList.size());
        Map<String, Object> assessmentIDCode1 = assessmentIDCodeList.get(0);
        Assert.assertNotNull(assessmentIDCode1);
        Assert.assertEquals("202A1", assessmentIDCode1.get("ID"));
        Assert.assertEquals("School", assessmentIDCode1.get("identificationSystem"));
        Assert.assertEquals("assigningOrganizationCode",
                assessmentIDCode1.get("assigningOrganizationCode"));

        Map<String, Object> assessmentIDCode2 = assessmentIDCodeList.get(1);
        Assert.assertNotNull(assessmentIDCode2);
        Assert.assertEquals("303A1", assessmentIDCode2.get("ID"));
        Assert.assertEquals("State", assessmentIDCode2.get("identificationSystem"));
        Assert.assertEquals("assigningOrganizationCode2",
                assessmentIDCode2.get("assigningOrganizationCode"));

        List<Map<String, Object>> assessmentPerfLevelList = (List<Map<String, Object>>) result
                .get(0).getBody().get("assessmentPerformanceLevel");
        Assert.assertNotNull(assessmentPerfLevelList);
        Assert.assertEquals(2, assessmentPerfLevelList.size());

        Map<String, Object> assessmentPerfLevel1 = assessmentPerfLevelList.get(0);
        Assert.assertNotNull(assessmentPerfLevel1);
        Assert.assertEquals(2400, assessmentPerfLevel1.get("maximumScore"));
        Assert.assertEquals(1600, assessmentPerfLevel1.get("minimumScore"));
        Assert.assertEquals("C-scaled scores",
                assessmentPerfLevel1.get("assessmentReportingMethod"));
        List<Map<String, Object>> perfLevelDescriptor1 = (List<Map<String, Object>>) assessmentPerfLevel1
                .get("performanceLevelDescriptor");
        Assert.assertEquals("description1", perfLevelDescriptor1.get(0).get("description"));

        Map<String, Object> assessmentPerfLevel2 = assessmentPerfLevelList.get(1);
        Assert.assertNotNull(assessmentPerfLevel2);
        Assert.assertEquals(2600, assessmentPerfLevel2.get("maximumScore"));
        Assert.assertEquals(1800, assessmentPerfLevel2.get("minimumScore"));
        Assert.assertEquals("ACT score", assessmentPerfLevel2.get("assessmentReportingMethod"));
        List<Map<String, Object>> perfLevelDescriptor2 = (List<Map<String, Object>>) assessmentPerfLevel2
                .get("performanceLevelDescriptor");
        Assert.assertEquals("description2", perfLevelDescriptor2.get(0).get("description"));

        Assert.assertEquals("Achievement test", result.get(0).getBody().get("assessmentCategory"));
        Assert.assertEquals("English", result.get(0).getBody().get("academicSubject"));
        Assert.assertEquals("Adult Education", result.get(0).getBody().get("gradeLevelAssessed"));
        Assert.assertEquals("Early Education",
                result.get(0).getBody().get("lowestGradeLevelAssessed"));
        Assert.assertEquals("SAT", result.get(0).getBody().get("contentStandard"));
        Assert.assertEquals("assessmentForm", result.get(0).getBody().get("assessmentForm"));
        Assert.assertEquals(1, result.get(0).getBody().get("version"));
        Assert.assertEquals("1999-01-01", result.get(0).getBody().get("revisionDate"));
        Assert.assertEquals(2400, result.get(0).getBody().get("maxRawScore"));
        Assert.assertEquals("nomenclature", result.get(0).getBody().get("nomenclature"));
    }

    @Test
    public void testAssessmentValidation() {
        NeutralRecord assessment = createAssessmentNeutralRecord(false);

        ReportStats reportStats = new SimpleReportStats();

        List<? extends Entity> result = transformer.transform(assessment, new DummyMessageReport(),
                reportStats);

        Entity entity = result.get(0);

        EntityTestUtils.mapValidation(entity.getBody(), "assessment", validator);
    }

    /**
     * @author tke Test the transformation and matching steps behave as expected
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testCreateAssessmentEntity() {
        EntityConfigFactory entityConfigurations = new EntityConfigFactory();
        MongoEntityRepository mockedEntityRepository = mock(MongoEntityRepository.class);
        NeutralRecord assessmentRC = createAssessmentNeutralRecord(true);

        entityConfigurations.setResourceLoader(new DefaultResourceLoader());
        entityConfigurations.setSearchPath("classpath:smooksEdFi2SLI/");
        transformer.setEntityRepository(mockedEntityRepository);

        transformer.setEntityConfigurations(entityConfigurations);

        // mock the Did Resolver
        DeterministicIdResolver mockDidResolver = Mockito.mock(DeterministicIdResolver.class);
        transformer.setDIdResolver(mockDidResolver);

        // Query assessmentQuery = new Query();
        // assessmentQuery.addCriteria(Criteria.where("body.assessmentTitle").is(ASSESSMENT_TITLE));
        // assessmentQuery.addCriteria(Criteria.where(METADATA_BLOCK + "." +
        // TENANT_ID_FIELD).is(TENANT_ID));
        // assessmentQuery.addCriteria(Criteria.where(METADATA_BLOCK + "." +
        // EXTERNAL_ID_FIELD).is(STUDENT_ID));

        List<Entity> le = new ArrayList<Entity>();
        le.add(createAssessmentEntity(true));

        when(
                mockedEntityRepository.findByQuery(eq("assessment"), Mockito.any(Query.class),
                        eq(0), eq(0))).thenReturn(le);
        AbstractMessageReport errorReport = new DummyMessageReport();
        ReportStats reportStats = new SimpleReportStats();

        List<SimpleEntity> res = transformer.handle(assessmentRC, errorReport, reportStats);

        verify(mockedEntityRepository).findByQuery(eq("assessment"), Mockito.any(Query.class),
                eq(0), eq(0));

        Assert.assertNotNull(res);
        Assert.assertEquals(ASSESSMENT_TITLE, res.get(0).getBody().get("assessmentTitle"));
        Assert.assertEquals(TENANT_ID, res.get(0).getMetaData().get(TENANT_ID_FIELD));
        Assert.assertEquals(STUDENT_ID, res.get(0).getMetaData().get(EXTERNAL_ID_FIELD));

    }

    /**
     * @author tke
     * @param setId
     *            : localId will be set if it is true
     * @return neutral record
     */
    private NeutralRecord createAssessmentNeutralRecord(boolean setId) {
        // Create neutral record for entity.
        NeutralRecord assessment = new NeutralRecord();

        if (setId) {
            assessment.setLocalId(STUDENT_ID);
        }

        // This will become tenantId field after transformed into neutral record
        // entity
        assessment.setSourceId(TENANT_ID);

        assessment.setRecordType("assessment");
        Map<String, Object> assessmentTitle = new HashMap<String, Object>();
        assessmentTitle.put("_value", "assessmentTitle");
        assessment.setAttributeField("AssessmentTitle", assessmentTitle);

        List<Map<String, Object>> assessmentIdentificationCodeList = new ArrayList<Map<String, Object>>();
        Map<String, Object> assessmentIdentificationCode1 = new HashMap<String, Object>();
        Map<String, Object> ID1 = new HashMap<String, Object>();
        ID1.put("_value", "202A1");
        assessmentIdentificationCode1.put("ID", ID1);
        assessmentIdentificationCode1.put("a_IdentificationSystem", "School");
        assessmentIdentificationCode1.put("a_AssigningOrganizationCode",
                "assigningOrganizationCode");
        Map<String, Object> assessmentIdentificationCode2 = new HashMap<String, Object>();
        Map<String, Object> ID2 = new HashMap<String, Object>();
        ID2.put("_value", "303A1");
        assessmentIdentificationCode2.put("ID", ID2);
        assessmentIdentificationCode2.put("a_IdentificationSystem", "State");
        assessmentIdentificationCode2.put("a_AssigningOrganizationCode",
                "assigningOrganizationCode2");
        assessmentIdentificationCodeList.add(assessmentIdentificationCode1);
        assessmentIdentificationCodeList.add(assessmentIdentificationCode2);
        assessment.setAttributeField("AssessmentIdentificationCode",
                assessmentIdentificationCodeList);

        Map<String, Object> assessmentCategory = new HashMap<String, Object>();
        assessmentCategory.put("_value", "Achievement test");
        assessment.setAttributeField("AssessmentCategory", assessmentCategory);
        assessment.setAttributeField("AssessmentFamilyReference", "AF-1");

        Map<String, Object> academicSubject = new HashMap<String, Object>();
        academicSubject.put("_value", "English");
        assessment.setAttributeField("AcademicSubject", academicSubject);
        Map<String, Object> gradeLevelAssessed = new HashMap<String, Object>();
        gradeLevelAssessed.put("_value", "Adult Education");
        assessment.setAttributeField("GradeLevelAssessed", gradeLevelAssessed);
        Map<String, Object> lowestGradeLevelAssessed = new HashMap<String, Object>();
        lowestGradeLevelAssessed.put("_value", "Early Education");
        assessment.setAttributeField("LowestGradeLevelAssessed", lowestGradeLevelAssessed);

        List<Map<String, Object>> assessmentPerformanceLevelList = new ArrayList<Map<String, Object>>();

        Map<String, Object> assessmentPerformanceLevel1 = new HashMap<String, Object>();
        Map<String, Object> maximumScore1 = new HashMap<String, Object>();
        maximumScore1.put("_value", 2400);
        assessmentPerformanceLevel1.put("MaximumScore", maximumScore1);
        Map<String, Object> minimumScore1 = new HashMap<String, Object>();
        minimumScore1.put("_value", 1600);
        assessmentPerformanceLevel1.put("MinimumScore", minimumScore1);
        Map<String, Object> assessmentReportingMethod1 = new HashMap<String, Object>();
        assessmentReportingMethod1.put("_value", "C-scaled scores");
        assessmentPerformanceLevel1.put("AssessmentReportingMethod", assessmentReportingMethod1);
        Map<String, Object> performanceLevel1 = new HashMap<String, Object>();
        Map<String, Object> description1 = new HashMap<String, Object>();
        description1.put("_value", "description1");
        performanceLevel1.put("Description", description1);
        assessmentPerformanceLevel1.put("PerformanceLevel", performanceLevel1);

        Map<String, Object> assessmentPerformanceLevel2 = new HashMap<String, Object>();
        Map<String, Object> maximumScore2 = new HashMap<String, Object>();
        maximumScore2.put("_value", 2600);
        assessmentPerformanceLevel2.put("MaximumScore", maximumScore2);
        Map<String, Object> minimumScore2 = new HashMap<String, Object>();
        minimumScore2.put("_value", 1800);
        assessmentPerformanceLevel2.put("MinimumScore", minimumScore2);
        Map<String, Object> assessmentReportingMethod2 = new HashMap<String, Object>();
        assessmentReportingMethod2.put("_value", "ACT score");
        assessmentPerformanceLevel2.put("AssessmentReportingMethod", assessmentReportingMethod2);
        Map<String, Object> performanceLevel2 = new HashMap<String, Object>();
        Map<String, Object> description2 = new HashMap<String, Object>();
        description2.put("_value", "description2");
        performanceLevel2.put("Description", description2);
        assessmentPerformanceLevel2.put("PerformanceLevel", performanceLevel2);

        assessmentPerformanceLevelList.add(assessmentPerformanceLevel1);
        assessmentPerformanceLevelList.add(assessmentPerformanceLevel2);
        assessment.setAttributeField("AssessmentPerformanceLevel", assessmentPerformanceLevelList);

        Map<String, Object> contentStandard = new HashMap<String, Object>();
        contentStandard.put("_value", "SAT");
        assessment.setAttributeField("ContentStandard", contentStandard);
        Map<String, Object> assessmentForm = new HashMap<String, Object>();
        assessmentForm.put("_value", "assessmentForm");
        assessment.setAttributeField("AssessmentForm", assessmentForm);
        Map<String, Object> version = new HashMap<String, Object>();
        version.put("_value", 1);
        assessment.setAttributeField("Version", version);
        Map<String, Object> revisionDate = new HashMap<String, Object>();
        revisionDate.put("_value", "1999-01-01");
        assessment.setAttributeField("RevisionDate", revisionDate);
        Map<String, Object> maxRawScore = new HashMap<String, Object>();
        maxRawScore.put("_value", 2400);
        assessment.setAttributeField("MaxRawScore", maxRawScore);
        Map<String, Object> nomenclature = new HashMap<String, Object>();
        nomenclature.put("_value", "nomenclature");
        assessment.setAttributeField("Nomenclature", nomenclature);

        return assessment;
    }

    /**
     * @author tke
     * @param setId
     *            :entityId will be set if it is true
     * @return assessment entity
     */
    public SimpleEntity createAssessmentEntity(boolean setId) {
        SimpleEntity entity = new SimpleEntity();

        if (setId) {
            entity.setEntityId(STUDENT_ID);
        }
        entity.setType("assessment");
        Map<String, Object> field = new HashMap<String, Object>();
        field.put("studentUniqueStateId", STUDENT_ID);
        field.put("Sex", "Male");
        field.put("assessmentTitle", ASSESSMENT_TITLE);
        field.put("assessmentFamilyReference", ASSESSMENT_FAMILY_ID);

        entity.setBody(field);
        entity.setMetaData(new HashMap<String, Object>());
        entity.getMetaData().put(TENANT_ID_FIELD, TENANT_ID);
        entity.getMetaData().put(EXTERNAL_ID_FIELD, STUDENT_ID);

        return entity;
    }

}
