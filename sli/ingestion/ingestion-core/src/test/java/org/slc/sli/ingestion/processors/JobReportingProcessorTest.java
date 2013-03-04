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

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.BatchJobStageType;
import org.slc.sli.ingestion.FaultType;
import org.slc.sli.ingestion.FileFormat;
import org.slc.sli.ingestion.FileType;
import org.slc.sli.ingestion.RangedWorkNote;
import org.slc.sli.ingestion.dal.NeutralRecordMongoAccess;
import org.slc.sli.ingestion.dal.NeutralRecordRepository;
import org.slc.sli.ingestion.model.Error;
import org.slc.sli.ingestion.model.Metrics;
import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.ResourceEntry;
import org.slc.sli.ingestion.model.Stage;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.util.BatchJobUtils;

/**
 *
 * @author bsuzuki
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/processor-test.xml" })
public class JobReportingProcessorTest {

    private static final String TEMP_DIR = "tmp/";
    private static final String OUTFILE = "job-test.ctl-11111111111111111.log";
    private static final String ERRORFILEPREFIX = "error.";

    private static final String BATCHJOBID = "test.ctl-11111111111111111";
    private static final String RESOURCEID = "InterchangeStudentParent.xml";
    private static final String NULLRESOURCEID = null;
    private static final int RECORDS_CONSIDERED = 50;
    private static final int RECORDS_FAILED = 5;
    private static final int RECORDS_PASSED = RECORDS_CONSIDERED - RECORDS_FAILED;
    private static final String DUP_ENTITY = "student";
    private static final Long DUP_COUNT = Long.valueOf(123);
    private static final String RECORDID = "recordIdentifier";
    private static final String ERRORDETAIL = "errorDetail";
    private static final String NULLERRORDETAIL = "null errorDetail";

    @InjectMocks
    JobReportingProcessor jobReportingProcessor = new JobReportingProcessor();

    @Mock
    private BatchJobDAO mockedBatchJobDAO;

    @Mock
    private NeutralRecordMongoAccess mockedNeutralRecordMongoAccess;

    private static File tmpDir = new File(TEMP_DIR);

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        tmpDir.mkdirs();

        jobReportingProcessor.setCommandTopicUri("seda:ingestion.command");
    }

    @After
    public void tearDown() throws Exception {
        if (tmpDir.exists()) {
            String[] files = tmpDir.list();
            for (String temp : files) {
                // construct the file structure
                File fileToDelete = new File(tmpDir, temp);
                fileToDelete.delete();
            }

            tmpDir.delete();
        }
    }

    @Test
    public void testProcess() throws Exception {

        // create fake mocked method return objects
        List<ResourceEntry> mockedResourceEntries = createFakeResourceEntries();
        List<Stage> mockedStages = createFakeStages();
        Map<String, String> mockedProperties = createFakeBatchProperties();
        NewBatchJob mockedJob = new NewBatchJob(BATCHJOBID, "192.168.59.11", "finished", 1, mockedProperties,
                mockedStages, mockedResourceEntries);
        mockedJob.setTopLevelSourceId(TEMP_DIR);

        Iterable<Error> fakeErrorIterable = createFakeErrorIterable();

        // mock the WorkNote
        RangedWorkNote workNote = RangedWorkNote.createSimpleWorkNote(BATCHJOBID);

        List<Stage> mockDeltaStage = new LinkedList<Stage>();
        List<Metrics> mockDeltaMetrics = new LinkedList<Metrics>();

        List<Stage> mockPersistenceStages = new LinkedList<Stage>();
        List<Metrics> mockPersistenceMetrics = new LinkedList<Metrics>();

        Metrics duplicateCountMetric = new Metrics(RESOURCEID, RECORDS_CONSIDERED, RECORDS_FAILED);
        HashMap<String, Long> dupMap = new HashMap<String, Long>();
        dupMap.put(DUP_ENTITY, DUP_COUNT);
        duplicateCountMetric.setDuplicateCounts(dupMap);
        mockDeltaMetrics.add(duplicateCountMetric);
        mockDeltaStage.add(new Stage(BatchJobStageType.DELTA_PROCESSOR.getName(), "Drop records that are duplicates", "finished",
                new Date(), new Date(), mockDeltaMetrics));

        mockPersistenceMetrics.add(new Metrics(RESOURCEID, RECORDS_CONSIDERED, RECORDS_FAILED));
        mockPersistenceStages.add(new Stage(BatchJobStageType.PERSISTENCE_PROCESSOR.getName(), "Persists records to the sli database", "finished",
                new Date(), new Date(), mockPersistenceMetrics));

        // set mocked BatchJobMongoDA in jobReportingProcessor
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(BATCHJOBID))).thenReturn(mockedJob);
        Mockito.when(mockedBatchJobDAO.getBatchJobStages(Matchers.eq(BATCHJOBID), Matchers.eq(BatchJobStageType.DELTA_PROCESSOR))).thenReturn(mockDeltaStage);
        Mockito.when(mockedBatchJobDAO.getBatchJobStages(Matchers.eq(BATCHJOBID), Matchers.eq(BatchJobStageType.PERSISTENCE_PROCESSOR))).thenReturn(mockPersistenceStages);
        Mockito.when(
                mockedBatchJobDAO.getBatchJobErrors(Matchers.eq(BATCHJOBID), Matchers.eq(RESOURCEID),
                        Matchers.eq(FaultType.TYPE_ERROR), Matchers.anyInt())).thenReturn(fakeErrorIterable);
        Mockito.when(
                mockedBatchJobDAO.getBatchJobErrors(Matchers.eq(BATCHJOBID), (String) Matchers.isNull(),
                        Matchers.eq(FaultType.TYPE_ERROR), Matchers.anyInt())).thenReturn(fakeErrorIterable);

        Mockito.when(
                mockedBatchJobDAO.getBatchJobErrors(Matchers.eq(BATCHJOBID), Matchers.eq(RESOURCEID),
                        Matchers.eq(FaultType.TYPE_WARNING), Matchers.anyInt())).thenReturn(fakeErrorIterable);

        NeutralRecordRepository mockedNeutralRecordRepository = Mockito.mock(NeutralRecordRepository.class);
        Mockito.when(mockedNeutralRecordMongoAccess.getRecordRepository()).thenReturn(mockedNeutralRecordRepository);

        // create exchange
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(workNote, RangedWorkNote.class);

        jobReportingProcessor.setBatchJobDAO(mockedBatchJobDAO);
        jobReportingProcessor.process(exchange);

        // read the generated job output file and check values
        FileReader fr = new FileReader(TEMP_DIR + OUTFILE);
        BufferedReader br = new BufferedReader(fr);

        // String contents = FileUtils.readFileToString(new File(TEMP_DIR + OUTFILE));

        assertTrue(br.readLine().contains("jobId: " + BATCHJOBID));
        assertTrue(br.readLine().contains(
                "[file] " + RESOURCEID + " (" + FileFormat.EDFI_XML.getCode() + "/"
                        + FileType.XML_STUDENT_PARENT_ASSOCIATION.getName() + ")"));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " records considered: " + RECORDS_CONSIDERED));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " records ingested successfully: " + RECORDS_PASSED));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " records failed: " + RECORDS_FAILED));
        assertTrue(br.readLine().contains("[file] " + RESOURCEID + " records failed xsd validation: " + 0));
        assertTrue(br.readLine().contains("[configProperty] purge: false"));
        // INFO  InterchangeStudentParent.xml student 123 deltas!
        assertTrue(br.readLine().contains(RESOURCEID + " " + DUP_ENTITY + " " + DUP_COUNT + " deltas!"));
        assertTrue(br.readLine().contains("Not all records were processed completely due to errors."));
        assertTrue(br.readLine().contains("Processed " + RECORDS_CONSIDERED + " records."));

        // read the generated error file and check values
        String errorFileName = getErrorFileName();
        fr = new FileReader(TEMP_DIR + errorFileName);
        br = new BufferedReader(fr);
        assertTrue(br.readLine().contains("ERROR  " + ERRORDETAIL));
        assertTrue(br.readLine().contains("ERROR  " + NULLERRORDETAIL));

        fr.close();
    }

    private String getErrorFileName() {
        if (tmpDir.exists()) {
            String[] files = tmpDir.list();

            for (String temp : files) {
                if (temp.startsWith(ERRORFILEPREFIX)) {
                    return temp;
                }
            }
        }
        return null;
    }

    private Map<String, String> createFakeBatchProperties() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("purge", "false");
        return map;
    }

    private List<ResourceEntry> createFakeResourceEntries() {
        List<ResourceEntry> resourceEntries = new LinkedList<ResourceEntry>();
        ResourceEntry re = new ResourceEntry();
        re.setResourceId(RESOURCEID);
        re.setExternallyUploadedResourceId(RESOURCEID);
        re.setResourceName(TEMP_DIR + RESOURCEID);
        re.update(FileFormat.EDFI_XML.getCode(), FileType.XML_STUDENT_PARENT_ASSOCIATION.getName(), "123456789",
                RECORDS_CONSIDERED, RECORDS_FAILED);
        resourceEntries.add(re);
        return resourceEntries;
    }

    private Iterable<Error> createFakeErrorIterable() {
        List<Error> errors = new LinkedList<Error>();
        Error error = new Error(BATCHJOBID, BatchJobStageType.PERSISTENCE_PROCESSOR.getName(), RESOURCEID,
                "10.81.1.27", "testhost", RECORDID, BatchJobUtils.getCurrentTimeStamp(),
                FaultType.TYPE_ERROR.getName(), "errorType", ERRORDETAIL);
        errors.add(error);

        Error nullError = new Error(BATCHJOBID, BatchJobStageType.PERSISTENCE_PROCESSOR.getName(), NULLRESOURCEID,
                "10.81.1.27", "testhost", RECORDID, BatchJobUtils.getCurrentTimeStamp(),
                FaultType.TYPE_ERROR.getName(), "errorType", NULLERRORDETAIL);
        errors.add(nullError);

        return errors;
    }

    private List<Stage> createFakeStages() {
        List<Metrics> fakeMetrics = new LinkedList<Metrics>();
        fakeMetrics.add(new Metrics(RESOURCEID, RECORDS_CONSIDERED, RECORDS_FAILED));

        List<Stage> fakeStageList = new LinkedList<Stage>();
        Stage s = new Stage(BatchJobStageType.PERSISTENCE_PROCESSOR.getName(), "Persists records to the sli databse",
                "finished", BatchJobUtils.getCurrentTimeStamp(), BatchJobUtils.getCurrentTimeStamp(), fakeMetrics);
        fakeStageList.add(s);
        return fakeStageList;
    }
}
