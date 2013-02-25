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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.dal.repository.MongoEntityRepository;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;

/**
 * Tests for LearningObjectiveTransform
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class LearningObjectiveTransformTest {

    @Autowired
    private LearningObjectiveTransform transform;

    @Mock
    private NeutralRecordMongoAccess mongoAccess = Mockito.mock(NeutralRecordMongoAccess.class);

    @Mock
    private NeutralRecordRepository repo = Mockito.mock(NeutralRecordRepository.class);

    @Mock
    private MongoEntityRepository mongoRepository = Mockito.mock(MongoEntityRepository.class);

    String transformCollection = LearningObjectiveTransform.LEARNING_OBJECTIVE + "_transformed";
    private String batchJobId = "10001";
    private Job job = mock(Job.class);

    @Before
    public void init() throws IOException, NoSuchFieldException {
        MockitoAnnotations.initMocks(this);

        RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote("batchJobId");
        transform.setWorkNote(workNote);

        when(mongoAccess.getRecordRepository()).thenReturn(repo);
        mongoAccess.setNeutralRecordRepository(repo);
        transform.setNeutralRecordMongoAccess(mongoAccess);

        transform.setMongoEntityRepository(mongoRepository);
        PrivateAccessor.setField(transform, "batchJobId", batchJobId);
        PrivateAccessor.setField(transform, "job", job);
        when(job.getTenantId()).thenReturn("SLI");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPerform() {
        String parentLO = "LearningObjectiveReference";
        NeutralRecord root = createNeutralRecord("root", "csn-0", "root objective", "academic subject", "fifth grade");
        NeutralRecord child1 = createNeutralRecord("child1", "csn-1", "child objective 1", "academic subject",
                "fifth grade");
        NeutralRecord child2 = createNeutralRecord("child2", "csn-2", "child objective 2", "academic subject",
                "fifth grade");
        NeutralRecord grandChild1 = createNeutralRecord("grandChild1", null, "grand child objective",
                "academic subject", "fifth grade");
        addChild(root, child1);
        addChild(child1, grandChild1);

        Iterable<NeutralRecord> nrList = Arrays.asList(root, child1, child2, grandChild1);

        Mockito.when(repo.findAllByQuery(Mockito.anyString(), Mockito.any(Query.class))).thenReturn(nrList);

        transform.perform(job);

        Map<String, Object> child1Parent = (Map<String, Object>) child1.getAttributes().get(
                parentLO);
        Map<String, Object> grandChildParent = (Map<String, Object>) grandChild1.getAttributes().get(
                parentLO);

        Assert.assertEquals("root objective",
                ((Map<String, Object>) ((Map<String, Object>) child1Parent.get("LearningObjectiveIdentity")).get("Objective")).get("_value"));
        Assert.assertEquals("child objective 1",
                ((Map<String, Object>) ((Map<String, Object>)  grandChildParent.get("LearningObjectiveIdentity")).get("Objective")).get("_value"));

        Assert.assertEquals(transformCollection, root.getRecordType());
        Assert.assertEquals(transformCollection, child1.getRecordType());
        Assert.assertEquals(transformCollection, child2.getRecordType());
        Assert.assertEquals(transformCollection, grandChild1.getRecordType());
    }

    private static NeutralRecord createNeutralRecord(String objectiveId, String contentStandardName, String objective,
            String subject, String grade) {
        NeutralRecord nr = new NeutralRecord();
        nr.setRecordType(LearningObjectiveTransform.LEARNING_OBJECTIVE);
        nr.setAttributes(new HashMap<String, Object>());
        nr.setLocalParentIds(new HashMap<String, Object>());
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.LO_ID_CODE_PATH, objectiveId);
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.LO_CONTENT_STANDARD_NAME_PATH, contentStandardName);
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.OBJECTIVE, objective);
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.ACADEMIC_SUBJECT, subject);
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.OBJECTIVE_GRADE_LEVEL, grade);
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.LEARNING_OBJ_REFS, new HashMap<String, Object>());
        return nr;
    }

    @SuppressWarnings("unchecked")
    private static void addChild(NeutralRecord parent, NeutralRecord child) {
        Map<String, Object> childRefs = new HashMap<String, Object>();
        Map<String, Object> map = new HashMap<String, Object>();
        setAtPath(map, "Objective", child.getAttributes().get("Objective"));
        setAtPath(map, "AcademicSubject", child.getAttributes().get("AcademicSubject"));
        setAtPath(map, "ObjectiveGradeLevel", child.getAttributes().get("ObjectiveGradeLevel"));
        Map<String, Object> identity = new HashMap<String, Object>();
        identity.put("LearningObjectiveIdentity", map);
        parent.setAttributeField("LearningObjectiveReference", identity);
    }

    @SuppressWarnings("unchecked")
    private static void setAtPath(Map<String, Object> map, String path, Object value) {
        String[] fields = path.split("\\.");
        for (int i = 0; i < fields.length - 1; i++) {
            Map<String, Object> subMap = (Map<String, Object>) map.get(fields[i]);
            if (subMap == null) {
                subMap = new HashMap<String, Object>();
                map.put(fields[i], subMap);
            }
            map = subMap;
        }
        map.put(fields[fields.length - 1], value);
    }
}
