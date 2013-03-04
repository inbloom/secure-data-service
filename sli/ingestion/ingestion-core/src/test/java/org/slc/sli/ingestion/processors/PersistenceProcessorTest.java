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

package org.slc.sli.ingestion.processors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.slc.sli.ingestion.util.NeutralRecordUtils.getByPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.util.NeutralRecordUtils;

/**
 * Tests for PersistenceProcessor
 *
 * @author dduran
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/processor-test.xml" })
public class PersistenceProcessorTest {

    @Autowired
    PersistenceProcessor processor;


    @Test
    public void testSortLearningObjectivesByDependency() {
        List<NeutralRecord> unsortedRecords = new ArrayList<NeutralRecord>();
        unsortedRecords.add(createNeutralRecord("objective1", null));
        unsortedRecords.add(createNeutralRecord("objective3", "objective2"));
        unsortedRecords.add(createNeutralRecord("objective4", "objective3"));
        unsortedRecords.add(createNeutralRecord("objective5", "objective4"));
        unsortedRecords.add(createNeutralRecord("objective6", null));
        unsortedRecords.add(createNeutralRecord("objective7", "objective6"));

        // do n! runs with random shuffle. shuffle won't cover every combination but does enough
        for (int count = 0; count < factorial(unsortedRecords.size()); count++) {

            Collections.shuffle(unsortedRecords);

            List<NeutralRecord> sortedRecords = PersistenceProcessor
                    .sortNrListByDependency(unsortedRecords, "learningObjective");

            for (int i = 0; i < sortedRecords.size(); i++) {
                NeutralRecord sortedRecord = sortedRecords.get(i);
                String parentObjectiveId = (String) sortedRecord.getLocalParentIds().get("parentObjectiveId");

                assertParentNotLaterInList(sortedRecords, i + 1, parentObjectiveId, "learningObjectiveId.identificationCode");
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCyclicLearningObjectivesByDependency() throws javax.jms.IllegalStateException {
        List<NeutralRecord> unsortedRecords = new ArrayList<NeutralRecord>();
        unsortedRecords.add(createNeutralRecord("objective1", "objective4"));
        unsortedRecords.add(createNeutralRecord("objective2", "objective1"));
        unsortedRecords.add(createNeutralRecord("objective3", "objective2"));
        unsortedRecords.add(createNeutralRecord("objective4", "objective3"));

        // do n! runs with random shuffle. shuffle won't cover every combination but does enough
        for (int count = 0; count < factorial(unsortedRecords.size()); count++) {

            Collections.shuffle(unsortedRecords);

            List<NeutralRecord> sortedRecords = PersistenceProcessor
                    .sortNrListByDependency(unsortedRecords, "learningObjective");

            assertNotNull(sortedRecords);
        }
    }

    private void assertParentNotLaterInList(List<NeutralRecord> sortedRecords, int startIndex, String parentId, String idPath) {
        if (parentId != null) {
            for (int i = startIndex; i < sortedRecords.size(); i++) {
                String sortedId = getByPath(idPath, sortedRecords.get(i).getAttributes());
                assertFalse("parent should not be after child in insertion order.", idPath.equals(sortedId));
            }
        }
    }

    private static NeutralRecord createNeutralRecord(String objectiveId, String parentObjectiveId) {
        String contentStandardName = "common core";

        Map<String, Object> learningObjectiveId = new HashMap<String, Object>();
        learningObjectiveId.put("identificationCode", objectiveId);
        learningObjectiveId.put("contentStandardName", contentStandardName);

        NeutralRecord record = new NeutralRecord();
        record.getAttributes().put("learningObjectiveId", learningObjectiveId);
        if (parentObjectiveId != null) {
            record.getLocalParentIds().put("parentObjectiveId", parentObjectiveId);
            record.getLocalParentIds().put("parentContentStandardName", contentStandardName);
        }
        return record;
    }

    @Test
    public void testSortLocalEducationAgencyByDependency() {
        List<NeutralRecord> unsortedRecords = new ArrayList<NeutralRecord>();
        unsortedRecords.add(createLEANeutralRecord("lea1", null));
        unsortedRecords.add(createLEANeutralRecord("lea1.1", "lea1"));
        unsortedRecords.add(createLEANeutralRecord("lea1.2", "lea1"));
        unsortedRecords.add(createLEANeutralRecord("lea1.2.1", "lea1.2"));
        unsortedRecords.add(createLEANeutralRecord("lea2", null));
        unsortedRecords.add(createLEANeutralRecord("lea2.1", "lea2"));

        // do n! runs with random shuffle. shuffle won't cover every combination but does enough
        for (int count = 0; count < factorial(unsortedRecords.size()); count++) {

            Collections.shuffle(unsortedRecords);

            List<NeutralRecord> sortedRecords = PersistenceProcessor
                    .sortNrListByDependency(unsortedRecords, "localEducationAgency");

            for (int i = 0; i < sortedRecords.size(); i++) {
                NeutralRecord sortedRecord = sortedRecords.get(i);
                String parentLEAId = NeutralRecordUtils.getByPath("localEducationAgencyReference", sortedRecord.getAttributes());

                assertParentNotLaterInList(sortedRecords, i + 1, parentLEAId, "stateOrganizationId");
            }
        }
    }

    private static NeutralRecord createLEANeutralRecord(String leaId, String parentLEAId) {
        NeutralRecord record = new NeutralRecord();
        record.getAttributes().put("localEducationAgencyReference", parentLEAId);
        record.getAttributes().put("stateOrganizationId", leaId);
        return record;
    }

    private static int factorial(int n) {
        if (n == 0) {
            return 1;
        }
        return n * factorial(n - 1);
    }

    @Test
    public void testRecordHashIngestedforSimpleEntity() {
        NeutralRecord originalRecord = createBaseNeutralRecord("simple");

        testRecordHashIngested(originalRecord, 1);
    }

    @Test
    public void testRecordHashIngestedforTransformedEntity() {
        NeutralRecord originalRecord = createBaseNeutralRecord("transformed");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rhData = (List<Map<String, Object>>) originalRecord.getMetaDataByName("rhData");
        testRecordHashIngested(originalRecord, rhData.size());
    }

    private void testRecordHashIngested(NeutralRecord originalRecord, int count) {
        recordHashTestPreConfiguration();

        processor.upsertRecordHash(originalRecord);
        verify(processor.getBatchJobDAO(), times(count)).insertRecordHash(any(String.class), any(String.class));

        processor.upsertRecordHash(addRecordHashMetadata(originalRecord));
        verify(processor.getBatchJobDAO(), times(count)).updateRecordHash(any(RecordHash.class), any(String.class));
    }

    private  NeutralRecord addRecordHashMetadata(NeutralRecord originalRecord) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rhDataList = (List<Map<String, Object>>)originalRecord.getMetaDataByName("rhData");
        for(Map<String, Object> rhDataItem: rhDataList) {
            Map<String, Object> hashData = new HashMap<String, Object>();
            hashData.put("id",         "id");
            hashData.put("hash",       "existingRecordHash");
            hashData.put("created",    new Long(1));
            hashData.put("updated",    new Long(1));
            hashData.put("version",    new Integer(1));
            hashData.put("tenantId",   "tenantId");
            rhDataItem.put("rhCurrentHash", hashData);
        }
        return originalRecord;
    }

    private void recordHashTestPreConfiguration() {
        BatchJobDAO batchJobDAO = Mockito.mock(BatchJobDAO.class);
        processor.setBatchJobDAO(batchJobDAO);

        Set<String> recordTypes = new HashSet<String>();
        recordTypes.add("recordType");
        processor.setRecordLvlHashNeutralRecordTypes(recordTypes);
    }

    private NeutralRecord createBaseNeutralRecord(String entityType) {
        NeutralRecord originalRecord = new NeutralRecord();
        originalRecord.setRecordType("recordType");

        List<Map<String, Object>> rhData = new ArrayList<Map<String, Object>>();

        if (entityType.equals("simple")) {
            Map<String, Object> rhDataElement = new HashMap<String, Object>();
            rhDataElement.put("rhId", "rhId1");
            rhDataElement.put("rhHash", "rhHash1");
            rhData.add(rhDataElement);
        } else if (entityType.equals("transformed")) {
            Map<String, Object> rhDataElement = new HashMap<String, Object>();
            rhDataElement.put("rhId", "rhId1");
            rhDataElement.put("rhHash", "rhHash1");
            rhData.add(rhDataElement);

            rhDataElement = new HashMap<String, Object>();
            rhDataElement.put("rhId", "rhId2");
            rhDataElement.put("rhHash", "rhHash2");
            rhData.add(rhDataElement);

            rhDataElement = new HashMap<String, Object>();
            rhDataElement.put("rhId", "rhId3");
            rhDataElement.put("rhHash", "rhHash3");
            rhData.add(rhDataElement);
        }

        originalRecord.addMetaData("rhData", rhData);
        originalRecord.addMetaData("rhTenantId", "rhTenantId");

        return originalRecord;
    }
}
