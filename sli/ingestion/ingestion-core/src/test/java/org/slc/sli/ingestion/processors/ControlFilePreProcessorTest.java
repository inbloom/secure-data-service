package org.slc.sli.ingestion.processors;


import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
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
import org.slc.sli.common.util.logging.SecurityEvent;
import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.landingzone.ControlFile;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.tenant.TenantDA;
import org.slc.sli.ingestion.util.BatchJobUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.net.InetAddress;
import java.net.InetAddress;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class ControlFilePreProcessorTest {


    @Autowired
    @InjectMocks
    private ControlFilePreProcessor controlFilePreProcessor;
    
    private static final String BATCHJOBID = "smallSample";
    private static final String tenantId = "Midgar";
    private static String tmp_dir = "ctl_tmp/";  
    private static String filename = "test.ctl";


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
        File fileForControlFile = new File(tmp_dir+filename);
   
        NewBatchJob mockedJob = new NewBatchJob(BATCHJOBID, "sourceId", "finished", 29, mockedProperties, mockedStages, null);

        mockedJob.setBatchProperties(mockedProperties);
        mockedJob.setSourceId("sourceId");
        mockedJob.setStatus("finished");
        mockedJob.setTenantId(tenantId);
 
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());

        exchange.getIn().setBody(fileForControlFile, File.class);
        exchange.getIn().setHeader("BatchJobId", BATCHJOBID);       

        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(BATCHJOBID))).thenReturn(mockedJob);   
        File lzFile = new File(mockedJob.getTopLevelSourceId());
        String lzPath = lzFile.getAbsolutePath();
        String absolutePath = new File(lzPath).getAbsolutePath();
        Mockito.when(mockedTenantDA.getTenantId(absolutePath)).thenReturn(tenantId);
        Mockito.when(mockedTenantDA.tenantDbIsReady(tenantId)).thenReturn(true);
        
        controlFilePreProcessor.setBatchJobDAO(mockedBatchJobDAO);
        controlFilePreProcessor.setTenantDA(mockedTenantDA);                

        controlFilePreProcessor.process(exchange);  
       
        Assert.assertEquals(1, mockedJob.getResourceEntries().size());
        Assert.assertEquals(29, mockedJob.getTotalFiles());     
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