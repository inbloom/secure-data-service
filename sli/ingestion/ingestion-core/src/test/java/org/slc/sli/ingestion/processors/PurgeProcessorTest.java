package org.slc.sli.ingestion.processors;

import java.util.HashSet;
import java.util.Set;

import junitx.util.PrivateAccessor;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.slc.sli.ingestion.model.NewBatchJob;
import org.slc.sli.ingestion.model.da.BatchJobDAO;
import org.slc.sli.ingestion.model.da.BatchJobMongoDA;

/**
 *
 * @author npandey
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext-test.xml" })
public class PurgeProcessorTest {

    private static final String BATCHJOBID = "MT.ctl-1234235235";

    @Autowired
    private PurgeProcessor purgeProcessor;

    @Test
    public void testNoTenantId() throws Exception {

        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("BatchJobId", BATCHJOBID);
        NewBatchJob mockedJob = new NewBatchJob(BATCHJOBID);


        // set mocked BatchJobMongoDA in purgeProcessor
        BatchJobDAO mockedBatchJobDAO = Mockito.mock(BatchJobMongoDA.class);
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(BATCHJOBID))).thenReturn(mockedJob);
        Mockito.when(mockedBatchJobDAO.saveBatchJob(mockedJob)).thenReturn(null);
        purgeProcessor.setBatchJobDAO(mockedBatchJobDAO);

        Logger log = Mockito.mock(org.slf4j.Logger.class);
        PrivateAccessor.setField(purgeProcessor, "logger", log);

        purgeProcessor.process(exchange);
        Mockito.verify(log, Mockito.atLeastOnce()).info("TenantId missing. No purge operation performed.");
    }

    @Test
    public void testPurging() throws Exception {
        NewBatchJob mockedJob = new NewBatchJob(BATCHJOBID);
        mockedJob.setProperty("tenantId", "SLI");

        // create exchange
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("BatchJobId", BATCHJOBID);

        // set mocked BatchJobMongoDA in purgeProcessor
        BatchJobDAO mockedBatchJobDAO = Mockito.mock(BatchJobMongoDA.class);
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(BATCHJOBID))).thenReturn(mockedJob);
        Mockito.when(mockedBatchJobDAO.saveBatchJob(mockedJob)).thenReturn(null);
        purgeProcessor.setBatchJobDAO(mockedBatchJobDAO);

        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        purgeProcessor.setMongoTemplate(mongoTemplate);

        Set<String> collectionNames = new HashSet<String>();
        collectionNames.add("student");
        collectionNames.add("teacher");

        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collectionNames);

        purgeProcessor.process(exchange);

        Mockito.verify(mongoTemplate, Mockito.atLeastOnce()).remove(Mockito.any(Query.class), Mockito.eq("student"));
    }

    @Test
    public void testPurgingSystemCollections() throws Exception {
        NewBatchJob mockedJob = new NewBatchJob(BATCHJOBID);
        mockedJob.setProperty("tenantId", "SLI");

        // create exchange
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("BatchJobId", BATCHJOBID);

        // set mocked BatchJobMongoDA in purgeProcessor
        BatchJobDAO mockedBatchJobDAO = Mockito.mock(BatchJobMongoDA.class);
        Mockito.when(mockedBatchJobDAO.findBatchJobById(Matchers.eq(BATCHJOBID))).thenReturn(mockedJob);
        Mockito.when(mockedBatchJobDAO.saveBatchJob(mockedJob)).thenReturn(null);
        purgeProcessor.setBatchJobDAO(mockedBatchJobDAO);

        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        purgeProcessor.setMongoTemplate(mongoTemplate);

        Set<String> collectionNames = new HashSet<String>();
        collectionNames.add("system.js");
        collectionNames.add("system.indexes");

        Mockito.when(mongoTemplate.getCollectionNames()).thenReturn(collectionNames);

        purgeProcessor.process(exchange);

        Mockito.verify(mongoTemplate, Mockito.never()).remove(Mockito.any(Query.class), Mockito.eq("system.js"));
    }

}
