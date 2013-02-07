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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import org.slc.sli.common.domain.NaturalKeyDescriptor;
import org.slc.sli.common.util.uuid.DeterministicUUIDGeneratorStrategy;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordWorkNote;
import org.slc.sli.ingestion.landingzone.AttributeType;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.RecordHash;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;
import org.slc.sli.ingestion.transformation.normalization.did.DeterministicIdResolver;
import org.slc.sli.ingestion.transformation.normalization.did.DidNaturalKey;
import org.slc.sli.ingestion.transformation.normalization.did.DidSchemaParser;

/**
 *
 * @author npandey
 *
 */

public class DeltaProcessorTest {

    @InjectMocks
    DeltaProcessor deltaProcessor = new DeltaProcessor();

    Set<String> recordLevelDeltaEnabledEntities = null;

    NewBatchJob job = null;

    @Mock
    protected BatchJobDAO batchJobDAO;

    @Mock
    private AbstractMessageReport databaseMessageReport;

    @Mock
    private DeterministicUUIDGeneratorStrategy dIdStrategy;

    @Mock
    private DeterministicIdResolver dIdResolver;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.initMocks(this);
        job = Mockito.mock(NewBatchJob.class);
        Mockito.when(job.getTenantId()).thenReturn("tenantId");
        Mockito.when(batchJobDAO.findBatchJobById(Mockito.anyString())).thenReturn(job);

        recordLevelDeltaEnabledEntities = new HashSet<String>();
        recordLevelDeltaEnabledEntities.add("section");
        recordLevelDeltaEnabledEntities.add("student");
        recordLevelDeltaEnabledEntities.add("grade");

        deltaProcessor.setdIdResolver(dIdResolver);
        deltaProcessor.setdIdStrategy(dIdStrategy);
        deltaProcessor.setRecordLevelDeltaEnabledEntities(recordLevelDeltaEnabledEntities);
    }

    @Test
    public void unSupportedEntitiesTest() throws Exception {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        List<NeutralRecord> neutralRecords = new ArrayList<NeutralRecord>();
        neutralRecords.add(createNeutralRecord("gradingPeriod"));

        NeutralRecordWorkNote workNote = new NeutralRecordWorkNote(neutralRecords, "batchJobId", "tenantId", false);

        exchange.getIn().setBody(workNote);

        deltaProcessor.process(exchange);

        NeutralRecordWorkNote newWorkNote = exchange.getIn().getBody(NeutralRecordWorkNote.class);
        List<NeutralRecord> filteredRecords = newWorkNote.getNeutralRecords();

        Assert.assertNotNull(filteredRecords);
        Assert.assertEquals(1, filteredRecords.size());
        Assert.assertEquals("gradingPeriod", filteredRecords.get(0).getRecordType());
    }

    @Test
    public void testDisableDeltaMode() throws Exception {
        Map<String, String> batchProperties = new HashMap<String, String>();
        batchProperties.put(AttributeType.DUPLICATE_DETECTION.getName(), RecordHash.RECORD_HASH_MODE_DISABLE);
        Mockito.when(job.getBatchProperties()).thenReturn(batchProperties);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        List<NeutralRecord> neutralRecords = new ArrayList<NeutralRecord>();
        neutralRecords.add(createNeutralRecord("gradingPeriod"));
        neutralRecords.add(createNeutralRecord("student"));
        neutralRecords.add(createNeutralRecord("grade"));

        NeutralRecordWorkNote workNote = new NeutralRecordWorkNote(neutralRecords, "batchJobId", "tenantId", false);

        exchange.getIn().setBody(workNote);

        deltaProcessor.process(exchange);

        NeutralRecordWorkNote newWorkNote = exchange.getIn().getBody(NeutralRecordWorkNote.class);
        List<NeutralRecord> filteredRecords = newWorkNote.getNeutralRecords();

        Assert.assertNotNull(filteredRecords);
        Assert.assertEquals(3, filteredRecords.size());
        Assert.assertEquals("gradingPeriod", filteredRecords.get(0).getRecordType());
        Assert.assertEquals("student", filteredRecords.get(1).getRecordType());
        Assert.assertEquals("grade", filteredRecords.get(2).getRecordType());
    }

    @Test
    public void testPrevIngested() throws Exception{

        DidSchemaParser didSchemaParser = Mockito.mock(DidSchemaParser.class);
        Mockito.when(dIdResolver.getDidSchemaParser()).thenReturn(didSchemaParser);

        Map<String, List<DidNaturalKey>> naturalKeys = new HashMap<String, List<DidNaturalKey>>();
        naturalKeys.put("student", new ArrayList<DidNaturalKey>());

        Mockito.when(didSchemaParser.getNaturalKeys()).thenReturn(naturalKeys);
        Mockito.when(dIdStrategy.generateId(Mockito.any(NaturalKeyDescriptor.class))).thenReturn("recordId");

        NeutralRecord nr = createNeutralRecord("student");
        List<NeutralRecord> neutralRecords = new ArrayList<NeutralRecord>();
        neutralRecords.add(nr);

        RecordHash recordHash = Mockito.mock(RecordHash.class);
        String recordHashValues = DigestUtils.shaHex(nr.getRecordType() + "-"
                + nr.getAttributes().toString() + "-" + "tenantId");
        Mockito.when(recordHash.getHash()).thenReturn(recordHashValues);
        Mockito.when(batchJobDAO.findRecordHash("tenantId", "recordId")).thenReturn(recordHash);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        NeutralRecordWorkNote workNote = new NeutralRecordWorkNote(neutralRecords, "batchJobId", "tenantId", false);
        exchange.getIn().setBody(workNote);

        deltaProcessor.process(exchange);

        NeutralRecordWorkNote newWorkNote = exchange.getIn().getBody(NeutralRecordWorkNote.class);
        List<NeutralRecord> filteredRecords = newWorkNote.getNeutralRecords();

        Assert.assertNotNull(filteredRecords);
        Assert.assertEquals(0, filteredRecords.size());

    }

    @Test
    public void testNotPrevIngested() throws Exception{

        DidSchemaParser didSchemaParser = Mockito.mock(DidSchemaParser.class);
        Mockito.when(dIdResolver.getDidSchemaParser()).thenReturn(didSchemaParser);
        Mockito.when(didSchemaParser.getNaturalKeys()).thenReturn(new HashMap<String, List<DidNaturalKey>>());
        Mockito.when(dIdStrategy.generateId(Mockito.any(NaturalKeyDescriptor.class))).thenReturn("recordId");

        NeutralRecord nr = createNeutralRecord("student");
        List<NeutralRecord> neutralRecords = new ArrayList<NeutralRecord>();
        neutralRecords.add(nr);

        Mockito.when(batchJobDAO.findRecordHash("tenantId", "recordId")).thenReturn(null);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        NeutralRecordWorkNote workNote = new NeutralRecordWorkNote(neutralRecords, "batchJobId", "tenantId", false);
        exchange.getIn().setBody(workNote);

        deltaProcessor.process(exchange);

        NeutralRecordWorkNote newWorkNote = exchange.getIn().getBody(NeutralRecordWorkNote.class);
        List<NeutralRecord> filteredRecords = newWorkNote.getNeutralRecords();

        Assert.assertNotNull(filteredRecords);
        Assert.assertEquals(1, filteredRecords.size());
        Assert.assertEquals("student", filteredRecords.get(0).getRecordType());

    }

    private NeutralRecord createNeutralRecord(String type) {
        NeutralRecord originalRecord = new NeutralRecord();
        originalRecord.setRecordType(type);
        originalRecord.setAttributes(new HashMap<String, Object>());
        return originalRecord;
    }
}
