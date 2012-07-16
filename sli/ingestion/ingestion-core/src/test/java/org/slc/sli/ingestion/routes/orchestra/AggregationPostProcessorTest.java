package org.slc.sli.ingestion.routes.orchestra;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.WorkNote;
import org.slc.sli.ingestion.landingzone.IngestionFileEntry;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
/**
*
* @author mpatel
*
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class AggregationPostProcessorTest {

    @Autowired
    AggregationPostProcessor aggregationPostProcessor;

    private static final String JOBID="MT.ctl-1234235235";
    private static final String RECORD_TYPE="testRecord";
    private static final List<String> FINISHED_ENTITIES = new ArrayList<String>();

    @Test
    public void testProcess() {
        Exchange preObject = new DefaultExchange(new DefaultCamelContext());

        IngestionFileEntry entry = Mockito.mock(IngestionFileEntry.class);
        NewBatchJob job = Mockito.mock(NewBatchJob.class);
        job.addFile(entry);

        BatchJobDAO dao = Mockito.mock(BatchJobDAO.class);
        FINISHED_ENTITIES.add("testRecord");
        Mockito.when(dao.getPersistedWorkNotes(JOBID)).thenReturn(FINISHED_ENTITIES);
        Mockito.when(dao.removeStagedEntityForJob(RECORD_TYPE, JOBID)).thenReturn(true);
        WorkNote workNote = WorkNote.createSimpleWorkNote(JOBID);
        preObject.getIn().setBody(workNote);

        aggregationPostProcessor.setBatchJobDAO(dao);

        try {
            aggregationPostProcessor.process(preObject);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Assert.assertEquals(workNote.getBatchJobId(), preObject.getIn().getBody(WorkNote.class).getBatchJobId());
        Assert.assertEquals(true, preObject.getIn().getHeader("processedAllStagedEntities"));

    }
}
