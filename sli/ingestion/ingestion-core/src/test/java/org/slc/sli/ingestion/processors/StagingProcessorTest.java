package org.slc.sli.ingestion.processors;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slc.sli.ingestion.NeutralRecord;
import org.slc.sli.ingestion.NeutralRecordWorkNote;
import org.slc.sli.ingestion.ResourceWriter;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.queues.MessageType;
import org.slc.sli.ingestion.reporting.AbstractMessageReport;

public class StagingProcessorTest {
    @InjectMocks
    StagingProcessor processor = new StagingProcessor();
    
    @Mock
    ResourceWriter<NeutralRecord> rwriter;
    
    @Mock
    protected BatchJobDAO batchJobDAO;
    
    @Mock
    private AbstractMessageReport databaseMessageReport;
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        NewBatchJob job = Mockito.mock(NewBatchJob.class);
        Mockito.when(job.getTenantId()).thenReturn("tenantId");
        Mockito.when(batchJobDAO.findBatchJobById(Mockito.anyString())).thenReturn(job);
    }
    
    @Test
    public void testProcess() throws Exception {
        NeutralRecord record1 = new NeutralRecord();
        record1.setRecordType("student");
        NeutralRecord record2 = new NeutralRecord();
        record2.setRecordType("student");
        List<NeutralRecord> records = new ArrayList<NeutralRecord>();
        records.add(record1);
        records.add(record2);
        
        NeutralRecordWorkNote workNote = new NeutralRecordWorkNote(records, "batchJobId", "tenantId", false);
        
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(workNote);
        
        processor.process(exchange);
        
        Assert.assertEquals(MessageType.DATA_STAGED.name(), exchange.getIn().getHeader("IngestionMessageType"));
        Assert.assertEquals(false, exchange.getIn().getHeader("hasErrors"));
        Mockito.verify(rwriter, Mockito.times(2)).insertResource(Mockito.any(NeutralRecord.class));
    }
}
