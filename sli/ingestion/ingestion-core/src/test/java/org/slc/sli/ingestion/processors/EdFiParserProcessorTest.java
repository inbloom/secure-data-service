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
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.Location;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.CamelTestSupport;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.core.io.Resource;

import org.slc.sli.ingestion.FileEntryWorkNote;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.parser.impl.RecordMetaImpl;
import org.slc.sli.ingestion.processors.EdFiParserProcessor.ParserState;
import org.slc.sli.ingestion.util.XsdSelector;

/**
 * EdFiParserProcessor unit tests.
 */
public class EdFiParserProcessorTest extends CamelTestSupport {

    @InjectMocks
    @Spy
    DummyEdFiParserProcessor processor = new DummyEdFiParserProcessor();

     private XsdSelector xsdSelector = new XsdSelector();

    @Mock
    private ProducerTemplate producer;

    @Mock
    protected BatchJobDAO batchJobDAO;

    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        NewBatchJob job = Mockito.mock(NewBatchJob.class);
        Mockito.when(job.getTenantId()).thenReturn("tenantId");
        Mockito.when(batchJobDAO.findBatchJobById(Mockito.anyString())).thenReturn(job);

        processor.setXsdSelector(xsdSelector);
        processor.setProducer(producer);
        processor.setBatchJobDAO(batchJobDAO);
        processor.setBatchSize(2);
        Mockito.doNothing().when(processor).parse(Mockito.any(InputStream.class), Mockito.any(Resource.class));
    }

    @Test
    public void testProcess() throws Exception {
        init();

        Exchange exchange = createFileEntryExchange();

        Resource xsd = Mockito.mock(Resource.class);
        Map<String, Resource> xsdList = new HashMap<String, Resource>();
        FileEntryWorkNote workNote = (FileEntryWorkNote) exchange.getIn().getMandatoryBody(WorkNote.class);
        xsdList.put(workNote.getFileEntry().getFileType().getName(), xsd);
        xsdSelector.setXsdList(xsdList);

        processor.process(exchange);

        Mockito.verify(processor, Mockito.times(1)).parse(Mockito.any(InputStream.class), Mockito.any(Resource.class));
    }

    @Test
    public void testVisitAndSend() throws Exception {
        init();
        Exchange exchange = createFileEntryExchange();

        processor.setUpState(exchange, exchange.getIn().getBody(FileEntryWorkNote.class));

        RecordMetaImpl meta = new RecordMetaImpl("student", "student");
        Location loc = Mockito.mock(Location.class);
        Mockito.when(loc.getLineNumber()).thenReturn(1);
        Mockito.when(loc.getColumnNumber()).thenReturn(1);
        meta.setSourceStartLocation(loc);
        meta.setSourceEndLocation(loc);

        Map<String, Object> body = new HashMap<String, Object>();
        body.put("studentUniqueStateId", "studentId");

        processor.visit(meta, body);

        ParserState state = processor.getState();
        List<NeutralRecord> records = state.getDataBatch();
        Assert.assertNotNull(records);
        Assert.assertEquals(1, records.size());
        Assert.assertEquals("studentId", records.get(0).getAttributes().get("studentUniqueStateId"));
        Assert.assertEquals("student", records.get(0).getRecordType());

    }

    private Exchange createFileEntryExchange() throws IOException {
        IngestionFileEntry ife = Mockito.mock(IngestionFileEntry.class);
        InputStream is = Mockito.mock(InputStream.class);
        Mockito.when(ife.getFileStream()).thenReturn(is);
        FileType type = FileType.XML_STUDENT_PARENT_ASSOCIATION;
        Mockito.when(ife.getFileType()).thenReturn(type);
        FileEntryWorkNote workNote = new FileEntryWorkNote("batchJobId", ife, false);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(workNote);

        return exchange;
    }

    private class DummyEdFiParserProcessor extends EdFiParserProcessor {
         public void setUpState(Exchange exchange, FileEntryWorkNote workNote){
             super.prepareState(exchange, workNote);
         }

         public ParserState getState() {
             return state.get();
         }
    }

}
