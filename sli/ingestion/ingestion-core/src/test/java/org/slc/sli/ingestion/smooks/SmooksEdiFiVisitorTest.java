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
package org.slc.sli.ingestion.smooks;

import static org.mockito.Matchers.any;

import java.io.IOException;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.milyn.container.ExecutionContext;
import org.milyn.javabean.context.BeanContext;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.ReportStats;
import org.slc.sli.ingestion.reporting.impl.DummyMessageReport;
import org.slc.sli.ingestion.reporting.impl.SimpleReportStats;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;

/**
 * Unit tests for the SmooksEdFilVisitor.
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class SmooksEdiFiVisitorTest {

    @Value("#{recordLvlHashNeutralRecordTypes}")
    private Set<String> recordLevelDeltaEnabledEntityNames;

    @Mock
    private DeterministicUUIDGeneratorStrategy mockDIdStrategy;
    @Mock
    private DeterministicIdResolver mockDIdResolver;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /*
     * TODO Add coverage for
     * SmooksEdFiVisitor.java
     * (low) visitAfter
     *      No test for SliDeltaManager.isPreviouslyIngested(...) returning true
     * (low) writeAndClearQueuedNeutralRecords
     * (low) visitBefore, onChildElement, and onChildText could throw Unsupported Exception
     */

    @Test
    public void testNonAssignedId() throws IOException {

        BatchJobDAO batchJobDAO = Mockito.mock(BatchJobDAO.class);

        // set up objects
        final String recordType = "otherType";
        final DummyMessageReport errorReport = new DummyMessageReport();
        final ReportStats reportStats = new SimpleReportStats();
        final IngestionFileEntry mockFileEntry = Mockito.mock(IngestionFileEntry.class);
        final String beanId = "ABeanId";
        final DeterministicUUIDGeneratorStrategy mockUUIDStrategy = Mockito
                .mock(DeterministicUUIDGeneratorStrategy.class);
        final ExecutionContext mockExecutionContext = Mockito.mock(ExecutionContext.class);
        SmooksEdFiVisitor visitor = SmooksEdFiVisitor.createInstance(beanId, "batchJobId", errorReport, reportStats, mockFileEntry);
        visitor.setRecordLevelDeltaEnabledEntities(recordLevelDeltaEnabledEntityNames);
        visitor.setBatchJobDAO(batchJobDAO);
        visitor.setDIdGeneratorStrategy(mockDIdStrategy);
        visitor.setDIdResolver(mockDIdResolver);

        BeanContext mockBeanContext = Mockito.mock(BeanContext.class);
        NeutralRecord neutralRecord = new NeutralRecord();
        neutralRecord.setRecordType(recordType);

        // set up behavior
        Mockito.when(mockExecutionContext.getBeanContext()).thenReturn(mockBeanContext);
        Mockito.when(mockBeanContext.getBean(beanId)).thenReturn(neutralRecord);
        Mockito.when(batchJobDAO.findRecordHash((String) Mockito.any(), (String) Mockito.any())).thenReturn(null);
        Mockito.when(mockDIdStrategy.generateId(any(NaturalKeyDescriptor.class))).thenReturn("recordId");

        // execute
        visitor.visitAfter(null, mockExecutionContext);

        // verify
        Mockito.verify(mockUUIDStrategy, Mockito.times(0)).generateId(Mockito.any(NaturalKeyDescriptor.class));

    }
}
