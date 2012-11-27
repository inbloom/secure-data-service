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

package org.slc.sli.ingestion.transformation.assessment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import org.slc.sli.domain.NeutralQuery;
import org.slc.sli.ingestion.Job;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;

/**
 * Tests for LearningObjectiveTransform
 *
 * @author Ryan Farris <rfarris@wgen.net>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LearningObjectiveTransformTest {

    @InjectMocks
    LearningObjectiveTransform transform = new LearningObjectiveTransform();

    @Mock
    NeutralRecordMongoAccess mongoAccess;

    @Mock
    Job job;

    @Mock
    NeutralRecordRepository repo;

    String transformCollection = LearningObjectiveTransform.LEARNING_OBJECTIVE + "_transformed";

    @Before
    public void init() {

    }

    @Ignore
    @Test
    public void testPerform() {
        String jobId = "JOB_ID";
        Mockito.when(job.getId()).thenReturn(jobId);
        Mockito.when(mongoAccess.getRecordRepository()).thenReturn(repo);

        NeutralRecord root = createNeutralRecord("root", "csn-0");
        NeutralRecord child1 = createNeutralRecord("child1", "csn-1");
        NeutralRecord child2 = createNeutralRecord("child2", "csn-2");
        NeutralRecord grandChild1 = createNeutralRecord("grandChild1", null);
        addChild(root, "child1", "csn-1");
        addChild(root, "child2", "csn-2");
        addChild(child1, "grandChild1", null);
        // List<NeutralRecord> nrList = Arrays.asList(root, child1, child2, grandChild1);
        Iterable<NeutralRecord> nrList = Arrays.asList(root, child1, child2, grandChild1);

        // Map<Object, NeutralRecord> nrMap = new HashMap<Object, NeutralRecord>();
        // nrMap.put(root.getRecordId(), root);
        // nrMap.put(child1.getRecordId(), child1);
        // nrMap.put(child2.getRecordId(), child2);
        // nrMap.put(grandChild1.getRecordId(), grandChild1);
        Mockito.when(repo.findAllForJob(Mockito.anyString(), Mockito.any(NeutralQuery.class)))
                .thenReturn(nrList);

        transform.perform(job);

        Mockito.verify(repo).createForJob(root);
        Mockito.verify(repo).createForJob(child1);
        Mockito.verify(repo).createForJob(child2);
        Mockito.verify(repo).createForJob(grandChild1);

        Assert.assertEquals("root", child1.getLocalParentIds().get(LearningObjectiveTransform.LOCAL_ID_OBJECTIVE_ID));
        Assert.assertEquals("root", child2.getLocalParentIds().get(LearningObjectiveTransform.LOCAL_ID_OBJECTIVE_ID));
        Assert.assertEquals("child1",
                grandChild1.getLocalParentIds().get(LearningObjectiveTransform.LOCAL_ID_OBJECTIVE_ID));

        Assert.assertEquals(transformCollection, root.getRecordType());
        Assert.assertEquals(transformCollection, child1.getRecordType());
        Assert.assertEquals(transformCollection, child2.getRecordType());
        Assert.assertEquals(transformCollection, grandChild1.getRecordType());
    }

    private static NeutralRecord createNeutralRecord(String objectiveId, String contentStandardName) {
        NeutralRecord nr = new NeutralRecord();
        nr.setRecordType(LearningObjectiveTransform.LEARNING_OBJECTIVE);
        nr.setAttributes(new HashMap<String, Object>());
        nr.setLocalParentIds(new HashMap<String, Object>());
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.LO_ID_CODE_PATH, objectiveId);
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.LO_CONTENT_STANDARD_NAME_PATH, contentStandardName);
        setAtPath(nr.getAttributes(), LearningObjectiveTransform.LEARNING_OBJ_REFS,
                new ArrayList<Map<String, Object>>());
        return nr;
    }

    @SuppressWarnings("unchecked")
    private static void addChild(NeutralRecord parent, String objectiveId, String contentStandardName) {
        List<Map<String, Object>> childRefs = (List<Map<String, Object>>) parent.getAttributes().get(
                LearningObjectiveTransform.LEARNING_OBJ_REFS);
        Map<String, Object> map = new HashMap<String, Object>();
        setAtPath(map, LearningObjectiveTransform.LO_ID_CODE_PATH, objectiveId);
        setAtPath(map, LearningObjectiveTransform.LO_CONTENT_STANDARD_NAME_PATH, contentStandardName);
        childRefs.add(map);
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
