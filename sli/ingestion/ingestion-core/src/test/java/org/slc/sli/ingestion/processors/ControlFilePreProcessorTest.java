package org.slc.sli.ingestion.processors;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.IngestionTest;
import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.BatchJobUtils;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ControlFilePreProcessorTest {


    @Autowired
    @InjectMocks
    private ControlFilePreProcessor controlFilePreProcessor;

    private static final String BATCHJOBID = "smallSample";
    private static final String tenantId = "Midgar";


    @Mock
    private BatchJobDAO mockedBatchJobDAO;

    @Mock
    private TenantDA mockedTenantDA;

    @Before
    public void setup() throws IOException {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testProcess() throws Exception {
        List<Stage> mockedStages = createFakeStages();
        Map<String, String> mockedProperties = createFakeBatchProperties();

        NewBatchJob job = new NewBatchJob(BATCHJOBID, "sourceId", "finished", 29, mockedProperties, mockedStages, null);

        job.setBatchProperties(mockedProperties);
        job.setSourceId("sourceId");
        job.setStatus("finished");
        job.setTenantId(tenantId);


        ResourceEntry entry = new ResourceEntry();
        File testZip = IngestionTest.getFile("ctl_tmp/test.zip");
        entry.setResourceName(testZip.getAbsolutePath());
        entry.setResourceFormat(FileFormat.ZIP_FILE.getCode());
        job.addResourceEntry(entry);

        WorkNote workNote = Mockito.mock(WorkNote.class);
        Mockito.when(workNote.getBatchJobId()).thenReturn(BATCHJOBID);

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        exchange.getIn().setBody(workNote);

        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(BATCHJOBID))).thenReturn(job);

        controlFilePreProcessor.setBatchJobDAO(mockedBatchJobDAO);
        controlFilePreProcessor.setTenantDA(mockedTenantDA);

        controlFilePreProcessor.process(exchange);

        Assert.assertEquals(2, job.getResourceEntries().size());
        Assert.assertEquals(29, job.getTotalFiles());
    }



    private List<Stage> createFakeStages() {
        List<Stage> fakeStageList = new LinkedList<Stage>();
        Stage s = new Stage(BatchJobStageType.ZIP_FILE_PROCESSOR.getName(), "Zip file validation",
                "finished", BatchJobUtils.getCurrentTimeStamp(), BatchJobUtils.getCurrentTimeStamp(), null);

        fakeStageList.add(s);
        return fakeStageList;
    }



    private Map<String, String> createFakeBatchProperties() {

        Map<String, String> map = new HashMap<String, String>();

        map.put("purge", "false");

        return map;

    }

}