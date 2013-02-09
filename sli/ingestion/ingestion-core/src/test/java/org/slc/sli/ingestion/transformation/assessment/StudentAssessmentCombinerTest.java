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

package org.slc.sli.ingestion.transformation.assessment;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.domain.NeutralCriteria;
import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;
import org.slc.sli.ingestion.transformation.TransformationStrategy;

/**
 * Test for combining student assessments
 *
 * @author nbrown
 *
 */
public class StudentAssessmentCombinerTest {

    private StudentAssessmentCombiner saCombiner = spy(new StudentAssessmentCombiner());

    private static final String STUDENT_OBJECTIVE_ASSESSMENT = "studentObjectiveAssessment";
    private static final String OBJECTIVE_ASSESSMENT = "objectiveAssessment";
    private static final String STUDENT_ASSESSMENT_REFERENCE = "studentTestAssessmentRef";
    private static final String OBJECTIVE_ASSESSMENT_REFERENCE = "objectiveAssessmentRef";
    private static final String STUDENT_ASSESSMENT_ITEMS_FIELD = "studentAssessmentItems";

    @Mock
    private NeutralRecordMongoAccess neutralRecordMongoAccess;

    @Mock
    private NeutralRecordRepository repository = Mockito.mock(NeutralRecordRepository.class);

    private String batchJobId = "10001";
    private Job job = mock(Job.class);

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);

        saCombiner.setNeutralRecordMongoAccess(neutralRecordMongoAccess);
        when(neutralRecordMongoAccess.getRecordRepository()).thenReturn(repository);

        when(repository.findAllForJob(eq(STUDENT_OBJECTIVE_ASSESSMENT), any(NeutralQuery.class))).thenReturn(
                buildSOANeutralRecords());

        when(repository.findAllForJob(eq(OBJECTIVE_ASSESSMENT), any(NeutralQuery.class))).thenReturn(
                Arrays.asList(AssessmentCombinerTest.buildTestObjAssmt(AssessmentCombinerTest.OBJ1_ID),
                        AssessmentCombinerTest.buildTestObjAssmt(AssessmentCombinerTest.OBJ2_ID)));
        DBCollection oaCollection = mock(DBCollection.class);
        when(repository.getCollectionForJob(STUDENT_OBJECTIVE_ASSESSMENT)).thenReturn(oaCollection);

        when(oaCollection.distinct(eq("body." + OBJECTIVE_ASSESSMENT_REFERENCE), any(BasicDBObject.class))).thenReturn(
                Arrays.asList(AssessmentCombinerTest.OBJ1_ID, AssessmentCombinerTest.OBJ2_ID));

        when(
                repository.findAllForJob(STUDENT_OBJECTIVE_ASSESSMENT, new NeutralQuery(new NeutralCriteria(
                        STUDENT_ASSESSMENT_REFERENCE, "=", "sa1")))).thenReturn(
                Arrays.asList(buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa1"),
                        buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa1")));
        when(
                repository.findAllForJob(STUDENT_OBJECTIVE_ASSESSMENT, new NeutralQuery(new NeutralCriteria(
                        STUDENT_ASSESSMENT_REFERENCE, "=", "sa2")))).thenReturn(
                Arrays.asList(buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa2"),
                        buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa2")));
        when(job.getId()).thenReturn(batchJobId);

        NeutralRecord assessmentItem = buildAssessmentItem("item-id");
        NeutralRecord sai = buildStudentAssessmentItem("item-id", "sa1");

        Map<String, String> paths = new HashMap<String, String>();
        paths.put("localParentIds.studentTestResultRef", "sa1");
        when(repository.findByPathsForJob("studentAssessmentItem", paths, batchJobId)).thenReturn(Arrays.asList(sai));

        Map<String, String> paths2 = new HashMap<String, String>();
        paths2.put("body.identificationCode", "item-id");
        when(repository.findByPathsForJob("assessmentItem", paths2, batchJobId)).thenReturn(
                Arrays.asList(assessmentItem));
    }

    @Ignore
    @Test
    public void testStudentObjectiveAssessment() throws IOException {
        Collection<NeutralRecord> sas = getTransformedEntities(saCombiner, job);
        boolean foundSai = false;
        for (NeutralRecord record : sas) {
            Assert.assertEquals(2, ((Collection<?>) record.getAttributes().get("studentObjectiveAssessments")).size());
            if (record.getAttributes().containsKey(STUDENT_ASSESSMENT_ITEMS_FIELD)) {
                foundSai = true;
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> saItems = (List<Map<String, Object>>) record.getAttributes().get(
                        STUDENT_ASSESSMENT_ITEMS_FIELD);
                Assert.assertEquals(1, saItems.size());

            }
        }
        Assert.assertEquals(2, sas.size());
        Assert.assertTrue(foundSai);
    }

    @SuppressWarnings("unchecked")
    public List<NeutralRecord> buildSANeutralRecords() {
        NeutralRecord sa1 = new NeutralRecord();
        sa1.setRecordType("studentAssessment");
        sa1.setAttributeField("administrationDate", "2011-05-01");
        Map<String, Object> scoreResult11 = new HashMap<String, Object>();
        scoreResult11.put("assessmentReportingMethod", "Raw Score");
        scoreResult11.put("result", 2400);
        Map<String, Object> scoreResult12 = new HashMap<String, Object>();
        scoreResult12.put("assessmentReportingMethod", "Percentile");
        scoreResult12.put("result", 99);
        sa1.setAttributeField("ScoreResults", Arrays.asList(scoreResult11, scoreResult12));
        sa1.setAttributeField("xmlId", "sa1");
        NeutralRecord sa2 = new NeutralRecord();
        sa2.setRecordType("studentAssessment");
        sa2.setAttributeField("administrationDate", "2011-05-01");
        Map<String, Object> scoreResult21 = new HashMap<String, Object>();
        scoreResult21.put("assessmentReportingMethod", "Raw Score");
        scoreResult21.put("result", 2400);
        Map<String, Object> scoreResult22 = new HashMap<String, Object>();
        scoreResult22.put("assessmentReportingMethod", "Percentile");
        scoreResult22.put("result", 99);
        sa2.setAttributeField("ScoreResults", Arrays.asList(scoreResult21, scoreResult22));
        sa2.setAttributeField("xmlId", "sa2");
        return Arrays.asList(sa1, sa2);
    }

    public List<NeutralRecord> buildSOANeutralRecords() {
        return Arrays.asList(buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa1"),
                buildSOANeutralRecord(AssessmentCombinerTest.OBJ1_ID, "sa2"),
                buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa1"),
                buildSOANeutralRecord(AssessmentCombinerTest.OBJ2_ID, "sa2"));
    }

    @SuppressWarnings("unchecked")
    private NeutralRecord buildSOANeutralRecord(String oaRef, String saRef) {
        NeutralRecord soa = new NeutralRecord();
        soa.setRecordType("studentObjectiveAssessment");
        Map<String, Object> scoreResult11 = new HashMap<String, Object>();
        scoreResult11.put("assessmentReportingMethod", "Raw Score");
        scoreResult11.put("result", 2400);
        Map<String, Object> scoreResult12 = new HashMap<String, Object>();
        scoreResult12.put("assessmentReportingMethod", "Percentile");
        scoreResult12.put("result", 99);
        soa.setAttributeField("ScoreResults", Arrays.asList(scoreResult11, scoreResult12));
        soa.setAttributeField("studentTestAssessmentRef", saRef);
        soa.setAttributeField("objectiveAssessmentRef", oaRef);
        return soa;
    }

    private NeutralRecord buildAssessmentItem(String id) {
        NeutralRecord rec = new NeutralRecord();
        rec.setRecordType("assessmentItem");
        rec.setAttributeField("identificationCode", id);
        return rec;
    }

    private NeutralRecord buildStudentAssessmentItem(String assessmentItemId, String studentAssessmentId) {
        NeutralRecord rec = new NeutralRecord();
        rec.setRecordType("studentAssessmentItem");
        rec.setAttributeField("assessmentResponse", "response");
        rec.getLocalParentIds().put("assessmentItemIdentificatonCode", assessmentItemId);
        rec.getLocalParentIds().put("studentTestResultRef", studentAssessmentId);
        return rec;
    }

    public Collection<NeutralRecord> getTransformedEntities(TransformationStrategy transformer, Job job)
            throws IOException {
        List<NeutralRecord> transformed = new ArrayList<NeutralRecord>();

        // Performing the transformation
        transformer.perform(job);
        Iterable<NeutralRecord> records = neutralRecordMongoAccess.getRecordRepository().findAllForJob(
                "studentAssessment", new NeutralQuery(0));
        Iterator<NeutralRecord> itr = records.iterator();
        NeutralRecord record = null;
        while (itr.hasNext()) {
            record = itr.next();
            transformed.add(record);
        }

        return transformed;
    }
}
