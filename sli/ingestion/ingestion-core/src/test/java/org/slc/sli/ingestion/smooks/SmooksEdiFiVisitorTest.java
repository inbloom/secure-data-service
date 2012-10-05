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
package org.slc.sli.ingestion.smooks;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.context.BeanContext;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.validation.DummyErrorReport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksEdiFiVisitorTest {

    @Value("${sli.ingestion.recordLevelDeltaEntities}")
    private String recordLevelDeltaEnabledEntityNames;

    @Test
    public void testSEADeterministicId() throws IOException {
        // set up objects
        final String recordType = "stateEducationAgency";
        testDeterministicId(recordType);
    }

    @Test
    public void testLEADeterministicId() throws IOException {
        // set up objects
        final String recordType = "localEducationAgency";
        testDeterministicId(recordType);
    }

    @Test
    public void testSchoolDeterministicId() throws IOException {
        // set up objects
        final String recordType = "school";
        testDeterministicId(recordType);
    }

    private void testDeterministicId(String recordType) throws IOException {

        BatchJobDAO batchJobDAO = Mockito.mock(BatchJobDAO.class);

        // set up objects
        final String deterministicId = "deterministicId";
        final String stateOrgId = "STATE_ID";
        final DummyErrorReport errorReport = new DummyErrorReport();
        final IngestionFileEntry mockFileEntry = Mockito.mock(IngestionFileEntry.class);
        final String tenantId = "ATenant";
        final String beanId = "ABeanId";
        final DeterministicUUIDGeneratorStrategy mockUUIDStrategy = Mockito
                .mock(DeterministicUUIDGeneratorStrategy.class);
        final ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);
        SmooksEdFiVisitor visitor = SmooksEdFiVisitor.createInstance(beanId, "batchJobId", errorReport, mockFileEntry,
                tenantId, mockUUIDStrategy);
        HashSet<String> recordLevelDeltaEnabledEntities = new HashSet<String>();
        recordLevelDeltaEnabledEntities.addAll(Arrays.asList(recordLevelDeltaEnabledEntityNames.split(",")));
        visitor.setRecordLevelDeltaEnabledEntities(recordLevelDeltaEnabledEntities);
        visitor.setBatchJobDAO(batchJobDAO);

        BeanContext mockBeanContext = Mockito.mock(BeanContext.class);
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordType(recordType);
        neutralRecord.setAttributeField("stateOrganizationId", stateOrgId);

        // set up behavior
        Mockito.when(mockExecutionContext.getBeanContext()).thenReturn(mockBeanContext);
        Mockito.when(mockBeanContext.getBean(beanId)).thenReturn(neutralRecord);
        Mockito.when(mockUUIDStrategy.generateId(Mockito.any(NaturalKeyDescriptor.class))).thenReturn(deterministicId);
        Mockito.when(batchJobDAO.findRecordHash((String) Mockito.any(), (String) Mockito.any())).thenReturn(null);

        // execute
        visitor.visitAfter(null, mockExecutionContext);

        // verify
        ArgumentCaptor<NaturalKeyDescriptor> argument = ArgumentCaptor.forClass(NaturalKeyDescriptor.class);
        Mockito.verify(mockUUIDStrategy, Mockito.times(1)).generateId(argument.capture());
        Assert.assertEquals(deterministicId, neutralRecord.getRecordId());
        NaturalKeyDescriptor desc = argument.getValue();
        Assert.assertEquals(tenantId, desc.getTenantId());
        Assert.assertEquals(recordType, desc.getEntityType());
        Map<String, String> keys = desc.getNaturalKeys();
        Assert.assertEquals(1, keys.size());
        Assert.assertEquals(stateOrgId, keys.get("stateOrganizationId"));

    }

    @Test
    public void testNonAssignedId() throws IOException {

        BatchJobDAO batchJobDAO = Mockito.mock(BatchJobDAO.class);

        // set up objects
        final String recordType = "otherType";
        final DummyErrorReport errorReport = new DummyErrorReport();
        final IngestionFileEntry mockFileEntry = Mockito.mock(IngestionFileEntry.class);
        final String tenantId = "ATenant";
        final String beanId = "ABeanId";
        final DeterministicUUIDGeneratorStrategy mockUUIDStrategy = Mockito
                .mock(DeterministicUUIDGeneratorStrategy.class);
        final ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);
        SmooksEdFiVisitor visitor = SmooksEdFiVisitor.createInstance(beanId, "batchJobId", errorReport, mockFileEntry,
                tenantId, mockUUIDStrategy);
        HashSet<String> recordLevelDeltaEnabledEntities = new HashSet<String>();
        recordLevelDeltaEnabledEntities.addAll(Arrays.asList(recordLevelDeltaEnabledEntityNames.split(",")));
        visitor.setRecordLevelDeltaEnabledEntities(recordLevelDeltaEnabledEntities);
        visitor.setBatchJobDAO(batchJobDAO);

        BeanContext mockBeanContext = Mockito.mock(BeanContext.class);
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordType(recordType);

        // set up behavior
        Mockito.when(mockExecutionContext.getBeanContext()).thenReturn(mockBeanContext);
        Mockito.when(mockBeanContext.getBean(beanId)).thenReturn(neutralRecord);
        Mockito.when(batchJobDAO.findRecordHash((String) Mockito.any(), (String) Mockito.any())).thenReturn(null);

        // execute
        visitor.visitAfter(null, mockExecutionContext);

        // verify
        Mockito.verify(mockUUIDStrategy, Mockito.times(0)).generateId(Mockito.any(NaturalKeyDescriptor.class));

    }
}
